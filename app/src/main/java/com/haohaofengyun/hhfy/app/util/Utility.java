package com.haohaofengyun.hhfy.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.haohaofengyun.hhfy.app.db.HHFYDB;
import com.haohaofengyun.hhfy.app.model.City;
import com.haohaofengyun.hhfy.app.model.County;
import com.haohaofengyun.hhfy.app.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 回到未来911 on 2015/12/1.
 */
public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvinceResponse(HHFYDB hhfydb, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    hhfydb.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的city数据
     */
    public synchronized static boolean handleCitiesResponse(HHFYDB hhfydb, String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] cities = response.split(",");
            if (cities != null && cities.length > 0) {
                for (String c : cities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    hhfydb.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的county数据
     */
    public synchronized static boolean handleCountiesResponse(HHFYDB hhfydb, String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] counties = response.split(",");
            if (counties.length > 0) {
                for (String c : counties) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    hhfydb.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析服务器返回的天气Jsons数据，并将信息存储到本地
     */
    public synchronized static void handleWeatherResponse(Context context, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weather = jsonObject.getJSONObject("weatherinfo");
            String cityName = weather.getString("city");
            String weatherCode = weather.getString("cityid");
            String temp1 = weather.getString("temp1");
            String temp2 = weather.getString("temp2");
            String weatherDesp = weather.getString("weather");
            String pTime = weather.getString("ptime");
            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, pTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存天气信息到SharedPreferences文件中
     */
    public synchronized static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp1, String temp2, String weatherDesp, String pTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_deps", weatherDesp);
        editor.putString("publish_time", pTime);
        editor.putString("current_date", sdf.format(new Date()));
        editor.apply();
    }
}
