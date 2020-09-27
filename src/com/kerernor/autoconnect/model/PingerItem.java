package com.kerernor.autoconnect.model;

import com.google.gson.annotations.Expose;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.SimpleDoubleProperty;

public class PingerItem {

    @Expose
    private String ipAddress;

    @Expose
    private String name;

    private SimpleDoubleProperty value;

    public PingerItem() {
        value = new SimpleDoubleProperty(0);
    }

    public PingerItem(String ipAddress, String name) {
        this.ipAddress = ipAddress;
        this.name = name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DoublePropertyBase getVal() {
        return value;
    }

    public void setProgressValue(double val) {
        value.set(val);
    }

    @Override
    public String toString() {
        return "PingerItem{" +
                "ipAddress='" + ipAddress + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
