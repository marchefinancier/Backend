package com.example.simulateurmarche.entities;

public enum Status {
    PLANIFIER("PLANIFIER"),
    EN_COURS("EN_COURS"),
    REALISER("REALISER"),
    ANNULER("ANNULER");
    private final String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }
}