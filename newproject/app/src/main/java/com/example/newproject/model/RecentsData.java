package com.example.newproject.model;

public class RecentsData {

    String placeName;
    String countryName;
    Integer imagerUrl;

    public RecentsData(String placeName, String countryName, Integer imagerUrl){
        this.placeName = placeName;
        this.countryName = countryName;
        this.imagerUrl = imagerUrl;
    }

    public Integer getImagerUrl() {
        return imagerUrl;
    }

    public void setImagerUrl(Integer imagerUrl) {
        this.imagerUrl = imagerUrl;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }
}
