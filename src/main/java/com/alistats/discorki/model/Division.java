package com.alistats.discorki.model;

public enum Division {
    IV,
    III,
    II,
    I;

    public int getDivisionLpValue() {
        return this.ordinal() * 100;   
    }
}
