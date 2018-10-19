package com.metrorez.myspace.user.model;

public class Extra {
    private String extraId;
    private String extraName;
    private double extraPrice;
    private boolean isAdded;

    public Extra() {
    }

    public Extra(String extraId, String extraName, double extraPrice) {
        this.extraName = extraName;
        this.extraPrice = extraPrice;
        this.extraId = extraId;
    }

    public String getExtraName() {
        return extraName;
    }

    public double getExtraPrice() {
        return extraPrice;
    }

    public String getExtraId() {
        return extraId;
    }

    public Extra(String extraId, String extraName, double extraPrice, boolean isAdded) {
        this.extraId = extraId;
        this.extraName = extraName;
        this.extraPrice = extraPrice;
        this.isAdded = isAdded;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public Extra(String extraName, double extraPrice, boolean isAdded) {
        this.extraName = extraName;
        this.extraPrice = extraPrice;
        this.isAdded = isAdded;
    }
}
