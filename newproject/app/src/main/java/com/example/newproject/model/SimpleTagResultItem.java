package com.example.newproject.model;

import java.util.List;

public class SimpleTagResultItem {
    private int imageResources;
    private String attrName;
    private String attrDistrict;
    private List<String> attrTags;

    public SimpleTagResultItem(int imageResources, String attrName, String attrDistrict, List<String> attrTags){
        this.imageResources = imageResources;
        this.attrName = attrName;
        this.attrDistrict = attrDistrict;
        this.attrTags = attrTags;
    }

    public int getImageResources() {
        return imageResources;
    }

    public void setImageResources(int imageResources) {
        this.imageResources = imageResources;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public List<String> getAttrTags() {
        return attrTags;
    }

    public void setAttrTags(List<String> attrTags) {
        this.attrTags = attrTags;
    }

    public String getAttrDistrict() {
        return attrDistrict;
    }

    public void setAttrDistrict(String attrDistrict) {
        this.attrDistrict = attrDistrict;
    }

}