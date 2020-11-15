package com.kerernor.autoconnect.model;

public class LastRemoteDriveItem {
    private String ip;
    private String path;

    public LastRemoteDriveItem(String ip, String path) {
        this.ip = ip;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
                ", path='" + path + '\'' +
                '}';
    }
}
