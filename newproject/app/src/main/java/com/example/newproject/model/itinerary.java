package com.example.newproject.model;

public class itinerary {
    public String getPlanID() {
        return planID;
    }

    public void setPlanID(String planID) {
        this.planID = planID;
    }

    String planID;
    String tripName;
    long startDate;
    long endDate;
    private String destinationCity;

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public itinerary(String tripName, long startDate, long endDate, String destinationCity, String planID) {
        this.tripName = tripName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.destinationCity = destinationCity;
        this.planID = planID;
    }
}