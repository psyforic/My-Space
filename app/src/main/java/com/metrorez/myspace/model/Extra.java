package com.metrorez.myspace.model;

public class Extra {
    private String extraName;
    private double extraPrice;

    public Extra() {
    }

    public Extra(String extraName, double extraPrice) {
        this.extraName = extraName;
        this.extraPrice = extraPrice;
    }

    public String getExtraName() {
        return extraName;
    }

    public double getExtraPrice() {
        return extraPrice;
    }
}
