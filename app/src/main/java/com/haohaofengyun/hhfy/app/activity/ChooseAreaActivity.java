package com.haohaofengyun.hhfy.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.haohaofengyun.hhfy.R;
import com.haohaofengyun.hhfy.app.db.HHFYDB;
import com.haohaofengyun.hhfy.app.model.City;
import com.haohaofengyun.hhfy.app.model.County;
import com.haohaofengyun.hhfy.app.model.Province;
import com.haohaofengyun.hhfy.app.util.HttpCallbackListener;
import com.haohaofengyun.hhfy.app.util.HttpUtil;
import com.haohaofengyun.hhfy.app.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends Activity {

    private static final String TAG = "Choose";

    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView textView;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private HHFYDB hhfydb;
    private List<String> dataList = new ArrayList<String>();
    /**
     * 省级列表
     */
    private List<Province> provincelist;
    /**
     * 市级列表
     */
    private List<City> cityList;
    /**
     * 县级列表
     */
    private List<County> countyList;
    /**
     * 选中的省
     */
    private Province selectedProvince;
    /**
     * 选中的市
     */
    private City selectedCity;
    /**
     * 当前级别
     */
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 标志位：是否从WeatherActivity跳转过来的
        boolean isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!isFromWeatherActivity && prefs.getBoolean("city_selected", false)) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_choose_area);
        textView = (TextView) findViewById(R.id.title_text);
        listView = (ListView) findViewById(R.id.list_view);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        hhfydb = HHFYDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provincelist.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    County county = countyList.get(position);
                    Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("countyCode", county.getCountyCode());
                    startActivity(intent);
                }
            }
        });
        queryProvinces();//加载省级数据
    }

    /**
     * 查询全国的省，先从数据库查询，如果没有再去服务器查询
     */
    private void queryProvinces() {
        provincelist = hhfydb.loadProvinces();
        if (provincelist.size() > 0) {
            dataList.clear();
            for (Province p : provincelist) {
                Log.d(TAG, "ProvinceName = " + p.getProvinceName());
                dataList.add(p.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            //listView.setSelection(0);
            textView.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer(null, "province");
        }
    }

    /**
     * 查询省内所有市，优先查询数据库，如果没有再去服务器查询
     */
    private void queryCities() {
        cityList = hhfydb.loadCities(selectedProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City c : cityList) {
                dataList.add(c.getCityName());
            }
            adapter.notifyDataSetChanged();
            // listView.setSelection(0);
            textView.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    /**
     * 查询市内所有县，优先查询数据库，如果没有再去服务器查询
     */
    private void queryCounties() {
        countyList = hhfydb.loadCounties(selectedCity.getId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County c : countyList) {
                dataList.add(c.getCountyName());
            }
            adapter.notifyDataSetChanged();
            // listView.setSelection(0);
            textView.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(selectedCity.getCityCode(), "county");
        }
    }

    /**
     * 从服务器查询
     */
    private void queryFromServer(String code, final String type) {
        String address;
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
            //address = "http://192.168.1.112:8000/AndroidSite/city.xml";
        }

        showProgressDialog();

        HttpUtil.sendHttpResquest(address, new HttpCallbackListener() {
            boolean result = false;

            @Override
            public void onFinish(String urlData) {
                switch (type) {
                    case "province":
                        result = Utility.handleProvinceResponse(hhfydb, urlData);
                        break;
                    case "city":
                        result = Utility.handleCitiesResponse(hhfydb, urlData, selectedProvince.getId());
                        break;
                    case "county":
                        result = Utility.handleCountiesResponse(hhfydb, urlData, selectedCity.getId());
                        break;
                }
                if (result) {
                    //回到UI线程
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            switch (type) {
                                case "province":
                                    queryProvinces();
                                    break;
                                case "city":
                                    queryCities();
                                    break;
                                case "county":
                                    queryCounties();
                                    break;
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                // 通过runOnUiThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,
                                "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * 捕获back按键，根据级别判断，返回省市列表，还是直接退出
     */
    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            finish();
        }
    }
}
