package com.haohaofengyun.hhfy.app.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haohaofengyun.hhfy.R;
import com.haohaofengyun.hhfy.app.util.HttpCallbackListener;
import com.haohaofengyun.hhfy.app.util.HttpUtil;
import com.haohaofengyun.hhfy.app.util.Utility;

public class WeatherActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout weatherInfoLayout;
    /**
     * 显示城市名称
     */
    private TextView cityNameText;
    /**
     * 显示发布时间
     */
    private TextView publishTimeText;
    /**
     * 显示当前日期
     */
    private TextView currentDateText;
    /**
     * 显示气温1
     */
    private TextView temp1Text;
    /**
     * 显示天禧描述
     */
    private TextView weatherInfoText;
    /**
     * 显示气温2
     */
    private TextView temp2Text;
    /**
     * 切换城市按钮
     */
    private Button switchCityButton;
    /**
     * 更新天气按钮
     */
    private Button refreshWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //初始化各个空间
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.text_city_name);
        publishTimeText = (TextView) findViewById(R.id.publish_time);
        currentDateText = (TextView) findViewById(R.id.now_date);
        temp1Text = (TextView) findViewById(R.id.temp11);
        temp2Text = (TextView) findViewById(R.id.temp12);
        weatherInfoText = (TextView) findViewById(R.id.weather_info);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);
        switchCityButton = (Button) findViewById(R.id.switch_city);

        String countyCode = getIntent().getStringExtra("countyCode");

        if (!TextUtils.isEmpty(countyCode)) {
            //有县级编码时就去查询天气编码
            publishTimeText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        } else {
            //没有县级编码时直接显示本地天气
            showWeather();
        }
        switchCityButton.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
    }

    /**
     * 读取本地天气数据并显示
     */
    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name", ""));
        publishTimeText.setText("今天"+prefs.getString("publish_time", "")+"发布");
        currentDateText.setText(prefs.getString("current_date", ""));
        weatherInfoText.setText(prefs.getString("weather_deps", ""));
        temp1Text.setText(prefs.getString("temp1", ""));
        temp2Text.setText(prefs.getString("temp2", ""));
        cityNameText.setVisibility(View.VISIBLE);
        weatherInfoLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 根据县级编码查询天气代码
     */
    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromServer(address, "countyCode");
    }

    /**
     * 根据天气编码查询天气数据
     */
    private void queryWeatherInfo(String weatherCode) {
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        queryFromServer(address, "weatherInfo");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_city:
                Intent intent = new Intent(WeatherActivity.this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity",true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                publishTimeText.setText("同步中...");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode = prefs.getString("weather_code","");
                if (!TextUtils.isEmpty(weatherCode)){
                    queryWeatherInfo(weatherCode);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 从服务器查询
     *
     * @param address
     * @param type
     */
    private void queryFromServer(final String address, final String type) {
        HttpUtil.sendHttpResquest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                //从县级编码返回数据解析出天气代码
                if (type.equals("countyCode")) {
                    String[] codes = response.split("\\|");
                    if (codes != null && codes.length == 2) {
                        String weatherCode = codes[1];
                        //查询天气信息
                        queryWeatherInfo(weatherCode);
                    }
                } else if (type.equals("weatherInfo")) {
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishTimeText.setText("同步失败");
                    }
                });
            }
        });
    }


}