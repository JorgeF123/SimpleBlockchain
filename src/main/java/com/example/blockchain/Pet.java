package com.example.blockchain;

public class Pet {
    private String id;
    private String name;
    private String type;
    private String color;
    private int rarity;
    private String owner;
    private long timeStamp;

    public Pet(String id, String name, String type, String color,
               int rarity, String owner, long timeStamp) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.color = color;
        this.rarity = rarity;
        this.owner = owner;
        this.timeStamp = timeStamp;
    }

    // Getters (needed for JSON serialization)
    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getColor() { return color; }
    public int getRarity() { return rarity; }
    public String getOwner() { return owner; }
    public long getTimeStamp() { return timeStamp; }
}
