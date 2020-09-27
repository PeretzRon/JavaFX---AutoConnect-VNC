package com.kerernor.autoconnect.model;

import java.util.List;

public class Pinger {

    private String name;
    private List<PingerItem> data = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PingerItem> getData() {
        return data;
    }

    public void setData(List<PingerItem> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Pinger{" +
                "name='" + name + '\'' +
                ", data=" + data +
                '}';
    }
}
