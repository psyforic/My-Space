package com.metrorez.myspace.model;

public class Extra {
    private String extraId;
    private String extraName;
    private double extraPrice;

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
}
