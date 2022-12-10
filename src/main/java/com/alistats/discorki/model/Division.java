package com.alistats.discorki.model;

public enum Division {
    IV(4),
    III(3),
    II(2),
    I(1);

    private Integer decimalRepresentation;
    private Division(Integer decimalRepresentation) {
        this.decimalRepresentation = decimalRepresentation;
    }

    public Integer getDivisionLpValue() {
        return this.ordinal() * 100;   
    }
}
