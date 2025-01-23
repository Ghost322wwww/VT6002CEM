package com.example.newproject;

public class Plan {

    String planID;
    String tripName;
    String destinationCity;
    long startDate;
    long endDate;
    public Plan() {

    }
    public Plan(String planID, String tripName, String destinationCity, long startDate, long endDate) {
        this.planID = planID;
        this.tripName = tripName;
        this.destinationCity = destinationCity;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getPlanID() {
        return planID;
    }

    public void setPlanID(String planID) {
        this.planID = planID;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
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

}