package com.kerernor.autoconnect.model;

public class LastConnectionItem {

    private String ip;

    public LastConnectionItem(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "LastConnectionItem{" +
                "ip='" + ip + '\'' +
                '}';
    }
}
