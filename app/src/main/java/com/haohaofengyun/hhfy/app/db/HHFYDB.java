package com.haohaofengyun.hhfy.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.haohaofengyun.hhfy.app.model.City;
import com.haohaofengyun.hhfy.app.model.County;
import com.haohaofengyun.hhfy.app.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 回到未来911 on 2015/11/29
 */
public class HHFYDB {
    /**
     * 数据库名称
     */
    private static final String DB_NAME = "HHFY";
    /**
     * 数据库版本
     */
    private static final int DB_VERSION = 1;

    private static HHFYDB hhfydb;
    private SQLiteDatabase db;

    /**
     * 将构造函数私有化
     */
    private HHFYDB(Context context) {
        HHFYOpenHelper hhfyOpenHelper = new HHFYOpenHelper(context, DB_NAME, null, DB_VERSION);
        db = hhfyOpenHelper.getWritableDatabase();
    }

    /**
     * 单例模式
     * 获取HHFYDB的实例
     */
    public synchronized static HHFYDB getInstance(Context context) {
        if (hhfydb == null) {
            hhfydb = new HHFYDB(context);
        }
        return hhfydb;
    }

    /**
     * 将province实例存入数据库
     */
    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues values = new ContentValues();
            values.put("province_name", province.getProvinceName());
            values.put("province_code", province.getProvinceCode());
            db.insert("Province", null, values);
        }
    }

    /**
     * 从数据库获取province列表
     */
    public List<Province> loadProvinces() {
        List<Province> list = new ArrayList<>();
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 将city实例存入数据库
     */
    public void saveCity(City city) {
        ContentValues values = new ContentValues();
        values.put("city_name", city.getCityName());
        values.put("city_code", city.getCityCode());
        values.put("province_id", city.getProvinceId());
        db.insert("City", null, values);
    }

    /**
     * 从数据库读取city列表
     */
    public List<City> loadCities(int provinceId) {
        List<City> list = new ArrayList<>();
        Cursor cursor = db.query("City", null, "province_id = ?", new String[]{String.valueOf(provinceId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
                list.add(city);
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 将county实例存入数据库
     */
    public void saveCounty(County county) {
        ContentValues values = new ContentValues();
        values.put("county_name", county.getCountyName());
        values.put("county_code", county.getCountyCode());
        values.put("city_id", county.getCityId());
        db.insert("County", null, values);
    }

    /**
     * 从数据库读取county列表
     */
    public List<County> loadCounties(int cityId) {
        List<County> list = new ArrayList<>();
        Cursor cursor = db.query("County", null, "city_id = ?", new String[]{String.valueOf(cityId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
                list.add(county);
            } while (cursor.moveToNext());
        }
        return list;
    }
}