package com.team10.trojancheckinout.model;

import java.lang.reflect.Field;

public class Building {
    private String id;
    private String name;
    private String qrCodeUrl;
    private int maxCapacity;
    private int currentCapacity;

    public Building() { }
    public Building(String id, String name, String qrCodeUrl, int maxCapacity) {
        this.id = id;
        this.name = name;
        this.qrCodeUrl = qrCodeUrl;
        this.maxCapacity = maxCapacity;
        this.currentCapacity = 0;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getQrCodeUrl() { return qrCodeUrl; }
    public int getMaxCapacity() { return maxCapacity; }
    public int getCurrentCapacity() { return currentCapacity; }
    public void setMaxCapacity(int capacity){maxCapacity = capacity;}

}
