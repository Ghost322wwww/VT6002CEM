package com.example.newproject;

import java.util.List;

public class Area {
    private String areaId;
    private String name;
    private List<District> districts;

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<District> getDistricts() {
        return districts;
    }

    public void setDistricts(List<District> districts) {
        this.districts = districts;
    }

    public Area(String areaId, String name, List<District> districts) {
        this.areaId = areaId;
        this.name = name;
        this.districts = districts;
    }

}
