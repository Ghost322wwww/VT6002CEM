package com.example.newproject;

public class District {
    private String districtId;
    private String name;

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public District(String districtId, String name) {
        this.districtId = districtId;
        this.name = name;
    }

}
