package com.haohaofengyun.hhfy.app.model;

/**
 * Created by 回到未来911 on 2015/11/30.
 */
public class Province {
    private int id;
    private String provinceName;
    private String provinceCode;

    public Province() {
    }

    public Province(int id, String provinceCode, String provinceName) {
        this.id = id;
        this.provinceCode = provinceCode;
        this.provinceName = provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

}
