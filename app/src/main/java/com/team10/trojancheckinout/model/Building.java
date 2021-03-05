package com.team10.trojancheckinout.model;

public class Building {
    private String id;
    private String name;

    public Building() { }
    public Building(String name) {
        // TODO: properly implement
        this.id = name;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
}
