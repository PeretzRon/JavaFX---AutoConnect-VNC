package com.kerernor.autoconnect.model;

import com.kerernor.autoconnect.util.KorTypes;

import java.util.UUID;

public class Computer {

    private final String id;
    private String ip;
    private String name;
    private String itemLocation;
    private KorTypes.ComputerType computerType;

    public Computer(String ip, String name, String itemLocation, KorTypes.ComputerType type) {
        this.ip = ip;
        this.name = name;
        this.itemLocation = itemLocation;
        this.computerType = type;
        this.id = UUID.randomUUID().toString();
    }

    public Computer(String ip, String name, String itemLocation, KorTypes.ComputerType type, String id) {
        this.ip = ip;
        this.name = name;
        this.itemLocation = itemLocation;
        this.computerType = type;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setComputerType(KorTypes.ComputerType computerType) {
        this.computerType = computerType;
    }

    public KorTypes.ComputerType getComputerType() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Computer computer = (Computer) o;
        return Objects.equals(id, computer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
