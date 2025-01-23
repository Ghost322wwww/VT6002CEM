package com.example.newproject.model;

public class UpcomingItiItem {

    private int imageResources;
    private String upcomingitidate;
    private String upcomingitiname;
    private String description;

    public UpcomingItiItem(){
    }
    public UpcomingItiItem(int upcomingitiimg, String upcomingitidate, String upcomingitiname, String description) {
        this.imageResources = upcomingitiimg;
        this.upcomingitidate = upcomingitidate;
        this.upcomingitiname = upcomingitiname;
        this.description = description;
    }

    public int getImageResources() {
        return imageResources;
    }

    public void setImageResources(int imageResources) {
        this.imageResources = imageResources;
    }

    public String getupcomingitidate() {
        return upcomingitidate;
    }

    public void setupcomingitidate(String upcomingitidate) {
        this.upcomingitidate = upcomingitidate;
    }

    public String getupcomingitiname() {
        return upcomingitiname;
    }

    public void setupcomingitiname(String upcomingitiname) {
        this.upcomingitiname = upcomingitiname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
