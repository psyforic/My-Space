package com.metrorez.myspace.model;

public class Inventory {
    private String inventoryId;
    private String itemName;
    private String roomName;

    public Inventory(String inventoryId, String itemName, String roomName) {
        this.inventoryId = inventoryId;
        this.itemName = itemName;
        this.roomName = roomName;
    }

    public Inventory(String itemName, String roomName) {
        this.itemName = itemName;
        this.roomName = roomName;
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getRoomName() {
        return roomName;
    }
}
