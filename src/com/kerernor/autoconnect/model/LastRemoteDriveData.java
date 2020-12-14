package com.kerernor.autoconnect.model;

import com.google.gson.Gson;
import com.kerernor.autoconnect.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LastRemoteDriveData {

    private static final LastRemoteDriveData instance = new LastRemoteDriveData();
    private ObservableList<LastRemoteDriveItem> lastRemoteDriveItemsList = FXCollections.observableArrayList();
    private Logger logger = LogManager.getLogger(LastRemoteDriveData.class);

    static {
//        LastRemoteDriveData.getInstance().getLastRemoteDriveItemsList().add(new LastRemoteDriveItem("10.0.0.10", "\\\\10.0.0.10\\c$"));
//        LastRemoteDriveData.getInstance().getLastRemoteDriveItemsList().add(new LastRemoteDriveItem("10.0.0.9", "\\\\10.0.0.9\\c$"));
//        LastRemoteDriveData.getInstance().getLastRemoteDriveItems().add(new LastRemoteDriveItem("10.0.0.8"))
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

    public ObservableList<LastRemoteDriveItem> getLastRemoteDriveItemsList() {
        return lastRemoteDriveItemsList;
    }

    public void addItemIfNotExist(LastRemoteDriveItem itemToAdd) {
        Optional<LastRemoteDriveItem> itemExistInList = lastRemoteDriveItemsList.stream().filter(item -> item.getIp().equals(itemToAdd.getIp())).findAny();
        itemExistInList.ifPresent(item -> lastRemoteDriveItemsList.remove(item));
        if (lastRemoteDriveItemsList.size() > Utils.MAX_REMOTE_DRIVE_CONNECT_LIST) {
            lastRemoteDriveItemsList.remove(lastRemoteDriveItemsList.size() - 1);
        }

        lastRemoteDriveItemsList.add(0, itemToAdd);
    }

    public void deleteItem(LastRemoteDriveItem itemToDelete) {
        lastRemoteDriveItemsList.remove(itemToDelete);
    }

    public void loadData() {
        logger.trace("LastRemoteDriveData.loadData");
        lastRemoteDriveItemsList = FXCollections.observableArrayList(); // FXCollection is for better performance
        LastRemoteDriveItem[] lastRemoteDriveItems = {};
        try (Reader reader = new InputStreamReader(new FileInputStream(Utils.LAST_CONNECTIONS_HISTORY_SHARE_DRIVE_DATA), StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
            lastRemoteDriveItems = gson.fromJson(reader, LastRemoteDriveItem[].class);
        } catch (IOException e) {
            logger.error("file not found - when the app closed, new file will created");
        }

        for (LastRemoteDriveItem lastRemoteDriveItem : lastRemoteDriveItems) {
            lastRemoteDriveItemsList.add(lastRemoteDriveItem);
        }
    }

    public void storeData() throws IOException {
        logger.trace("storeData");
        Path path = Paths.get(Utils.LAST_CONNECTIONS_HISTORY_SHARE_DRIVE_DATA);
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            List<LastRemoteDriveItem> lastRemoteDriveItems = new ArrayList<>(lastRemoteDriveItemsList);
            Gson gson = new Gson();
            String data = gson.toJson(lastRemoteDriveItems);
            bw.write(data);
        }
    }
}
