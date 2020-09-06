package com.kerernor.autoconnect.models;

public class Computer {

    private String ip;
    private String name;
    private String itemLocation;
    private eComputerType computerType;

    public Computer(String ip, String name, String itemLocation, eComputerType type) {
        this.ip = ip;
        this.name = name;
        this.itemLocation = itemLocation;
        this.computerType = type;
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
}
