package com.kerernor.autoconnect.model;

import java.util.UUID;

public class Computer {

    private final String id;
    private String ip;
    private String name;
    private String itemLocation;
    private eComputerType computerType;

    public Computer(String ip, String name, String itemLocation, eComputerType type) {
        this.ip = ip;
        this.name = name;
        this.itemLocation = itemLocation;
        this.computerType = type;
        this.id = UUID.randomUUID().toString();
    }

    public Computer(String ip, String name, String itemLocation, eComputerType type, String id) {
        this.ip = ip;
        this.name = name;
        this.itemLocation = itemLocation;
        this.computerType = type;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setComputerType(eComputerType computerType) {
        this.computerType = computerType;
    }

    public eComputerType getComputerType() {
        return computerType;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItemLocation() {
        return itemLocation;
    }

    public void setLocation(String location) {
        itemLocation = location;
    }

    @Override
    public String toString() {
        return name;
    }
}
