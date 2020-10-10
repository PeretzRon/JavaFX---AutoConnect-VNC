package com.kerernor.autoconnect.model;

import java.util.List;

public class Pinger {

    private String name;
    private List<PingerItem> data = null;

    public String getName() {
        return name;
    }

    public Pinger(String name, List<PingerItem> data) {
        this.name = name;
        this.data = data;
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
        return name;
    }
}
