package com.kerernor.autoconnect.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LastConnectionData {

    private static final LastConnectionData instance = new LastConnectionData();
    private ObservableList<LastConnectionItem> lastConnectionItems = FXCollections.observableArrayList();

    private LastConnectionData() {
        lastConnectionItems.add(new LastConnectionItem("10.0.0.10"));
        lastConnectionItems.add(new LastConnectionItem("10.0.0.20"));
        lastConnectionItems.add(new LastConnectionItem("255.255.255.255"));
    }

    public static LastConnectionData getInstance() {
        return instance;
    }

    public ObservableList<LastConnectionItem> getLastConnectionItems() {
        return lastConnectionItems;
    }
}
