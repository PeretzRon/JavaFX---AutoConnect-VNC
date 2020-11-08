package com.kerernor.autoconnect.model;

public class LastRemoteDriveItem {
    private String ip;

    public LastRemoteDriveItem(String ip) {
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
        return "LastRemoteDriveItem{" +
                "ip='" + ip + '\'' +
                '}';
    }
}
