package com.example.newproject.model;

public class SearchResultItem {

    private int imageResource;
    private String name;
    private String address;

    public SearchResultItem(int imageResource, String name, String address) {
        this.imageResource = imageResource;
        this.name = name;
        this.address = address;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}