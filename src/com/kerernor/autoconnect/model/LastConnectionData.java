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
        lastConnectionItems.add(new LastConnectionItem("179.103.141.51"));
        lastConnectionItems.add(new LastConnectionItem("144.155.123.341"));
    }

    public static LastConnectionData getInstance() {
        return instance;
    }

    public void removeHistoryItem(LastConnectionItem itemToDelete) {
        lastConnectionItems.remove(itemToDelete);
    }

    public ObservableList<LastConnectionItem> getLastConnectionItems() {
        return lastConnectionItems;
    }
}
