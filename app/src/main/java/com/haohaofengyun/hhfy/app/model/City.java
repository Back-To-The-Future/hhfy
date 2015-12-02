package com.haohaofengyun.hhfy.app.model;

/**
 * Created by 回到未来911 on 2015/11/30.
 */
public class City {
    private int id;
    private String cityName;
    private String cityCode;
    private int provinceId;

    public City() {
    }

    public City(String cityCode, String cityName, int id, int provinceId) {
        this.cityCode = cityCode;
        this.cityName = cityName;
        this.id = id;
        this.provinceId = provinceId;
    }

    public String getCityCode() {
        return cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public int getId() {
        return id;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
