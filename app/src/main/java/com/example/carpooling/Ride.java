package com.example.carpooling;

import java.util.List;

public class Ride {
    private String driverName;
    private String fromLocation;
    private String toLocation;
    private String phoneNumber;
    private String dateTime;
    private int totalSeats;
    private int availableSeats;
    private double price;
    private String carModel;
    private String carPhotoUrl;
    private String documentId;

    private List<String> travelersNames;
    private List<String> travelersPhones;
    private List<String> travelersUids; // Updated to include travelersUids
    private String userId; // New field to store userId

    public Ride() {
        // Required empty public constructor
    }

    // Getters and Setters
    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarPhotoUrl() {
        return carPhotoUrl;
    }

    public void setCarPhotoUrl(String carPhotoUrl) {
        this.carPhotoUrl = carPhotoUrl;
    }

    public List<String> getTravelersNames() {
        return travelersNames;
    }

    public List<String> getTravelersPhones() {
        return travelersPhones;
    }

    public List<String> getTravelersUids() {
        return travelersUids;
    }

    public void setTravelersUids(List<String> travelersUids) {
        this.travelersUids = travelersUids;
    }
    public void setTravelersNames(List<String> travelersNames) {
        this.travelersNames = travelersNames;
    }
    public void setTravelersPhones(List<String> travelersPhones) {
        this.travelersPhones = travelersPhones;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
