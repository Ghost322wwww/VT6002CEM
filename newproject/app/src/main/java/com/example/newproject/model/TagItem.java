package com.example.newproject.model;

public class TagItem {
    private int imageResource;
    private String text;

    public TagItem(int imageResource, String text) {
        this.imageResource = imageResource;
        this.text = text;
    }
    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
