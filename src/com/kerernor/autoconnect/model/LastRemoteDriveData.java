package com.kerernor.autoconnect.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.log4j.Logger;

public class LastRemoteDriveData {

    private static final LastRemoteDriveData instance = new LastRemoteDriveData();
    private ObservableList<LastRemoteDriveItem> lastRemoteDriveItems = FXCollections.observableArrayList();
    private Logger logger = Logger.getLogger(LastRemoteDriveData.class);

    static {
       LastRemoteDriveData.getInstance().getLastRemoteDriveItems().add(new LastRemoteDriveItem("10.0.0.10"));
       LastRemoteDriveData.getInstance().getLastRemoteDriveItems().add(new LastRemoteDriveItem("10.0.0.9"));
       LastRemoteDriveData.getInstance().getLastRemoteDriveItems().add(new LastRemoteDriveItem("10.0.0.8"));
       LastRemoteDriveData.getInstance().getLastRemoteDriveItems().add(new LastRemoteDriveItem("10.0.0.7"));
       LastRemoteDriveData.getInstance().getLastRemoteDriveItems().add(new LastRemoteDriveItem("10.0.0.6"));
       LastRemoteDriveData.getInstance().getLastRemoteDriveItems().add(new LastRemoteDriveItem("10.0.0.4"));
       LastRemoteDriveData.getInstance().getLastRemoteDriveItems().add(new LastRemoteDriveItem("10.0.0.2"));
       LastRemoteDriveData.getInstance().getLastRemoteDriveItems().add(new LastRemoteDriveItem("10.0.0.1"));
       LastRemoteDriveData.getInstance().getLastRemoteDriveItems().add(new LastRemoteDriveItem("10.0.0.59"));
    }

    public static LastRemoteDriveData getInstance() {
        return instance;
    }

    public ObservableList<LastRemoteDriveItem> getLastRemoteDriveItems() {
        return lastRemoteDriveItems;
    }
}
