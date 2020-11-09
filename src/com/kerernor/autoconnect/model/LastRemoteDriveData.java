package com.kerernor.autoconnect.model;

import com.kerernor.autoconnect.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.log4j.Logger;

import java.util.Optional;

public class LastRemoteDriveData {

    private static final LastRemoteDriveData instance = new LastRemoteDriveData();
    private ObservableList<LastRemoteDriveItem> lastRemoteDriveItems = FXCollections.observableArrayList();
    private Logger logger = Logger.getLogger(LastRemoteDriveData.class);

    static {
//        LastRemoteDriveData.getInstance().getLastRemoteDriveItems().add(new LastRemoteDriveItem("10.0.0.10"));
//        LastRemoteDriveData.getInstance().getLastRemoteDriveItems().add(new LastRemoteDriveItem("10.0.0.9"));
//        LastRemoteDriveData.getInstance().getLastRemoteDriveItems().add(new LastRemoteDriveItem("10.0.0.8"));
//        LastRemoteDriveData.getInstance().getLastRemoteDriveItems().add(new LastRemoteDriveItem("10.0.0.7"));
//        LastRemoteDriveData.getInstance().getLastRemoteDriveItems().add(new LastRemoteDriveItem("10.0.0.6"));
//        LastRemoteDriveData.getInstance().getLastRemoteDriveItems().add(new LastRemoteDriveItem("10.0.0.4"));
//        LastRemoteDriveData.getInstance().getLastRemoteDriveItems().add(new LastRemoteDriveItem("10.0.0.2"));
//        LastRemoteDriveData.getInstance().getLastRemoteDriveItems().add(new LastRemoteDriveItem("10.0.0.1"));
//        LastRemoteDriveData.getInstance().getLastRemoteDriveItems().add(new LastRemoteDriveItem("10.0.0.59"));
    }

    public static LastRemoteDriveData getInstance() {
        return instance;
    }

    public ObservableList<LastRemoteDriveItem> getLastRemoteDriveItems() {
        return lastRemoteDriveItems;
    }

    public void addItemIfNotExist(LastRemoteDriveItem itemToAdd) {
        Optional<LastRemoteDriveItem> itemExistInList = lastRemoteDriveItems.stream().filter(item -> item.getIp().equals(itemToAdd.getIp())).findAny();
        itemExistInList.ifPresent(item -> lastRemoteDriveItems.remove(item));
        if (lastRemoteDriveItems.size() > Utils.MAX_REMOTE_DRIVE_CONNECT_LIST) {
            lastRemoteDriveItems.remove(lastRemoteDriveItems.size() - 1);
        }

        lastRemoteDriveItems.add(0, itemToAdd);
    }
}
