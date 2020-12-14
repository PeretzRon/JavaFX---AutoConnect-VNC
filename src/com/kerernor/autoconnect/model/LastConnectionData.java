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

public class LastConnectionData {

    private static final LastConnectionData instance = new LastConnectionData();
    private ObservableList<LastConnectionItem> lastConnectionItems = FXCollections.observableArrayList();
    private Logger logger = LogManager.getLogger(LastConnectionData.class);

    private LastConnectionData() {
    }

    public static LastConnectionData getInstance() {
        return instance;
    }

    public void removeHistoryItem(LastConnectionItem itemToDelete) {
        lastConnectionItems.remove(itemToDelete);
    }

    public void addHistoryItemIfNotExist(LastConnectionItem itemToAdd) {
        Optional<LastConnectionItem> itemExistInList = lastConnectionItems.stream().filter(item -> item.getIp().equals(itemToAdd.getIp())).findAny();
        itemExistInList.ifPresent(item -> lastConnectionItems.remove(item));
        if (lastConnectionItems.size() > Utils.MAX_HISTORY_CONNECT_LIST) {
            lastConnectionItems.remove(lastConnectionItems.size() - 1);
        }
        lastConnectionItems.add(0, itemToAdd);
    }

    public void storeData() throws IOException {
        logger.trace("LastConnectionData.storeData");
        Path path = Paths.get(Utils.LAST_CONNECTIONS_HISTORY_DATA);
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            List<LastConnectionItem> lastConnectionItemsList = new ArrayList<>(lastConnectionItems);
            Gson gson = new Gson();
            String data = gson.toJson(lastConnectionItemsList);
            bw.write(data);
        }
    }

    public void loadData() {
        logger.trace("LastConnectionData.loadData");
        lastConnectionItems = FXCollections.observableArrayList(); // FXCollection is for better performance
        LastConnectionItem[] lastConnectionItemsList = {};
        try (Reader reader = new InputStreamReader(new FileInputStream(Utils.LAST_CONNECTIONS_HISTORY_DATA), StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
            lastConnectionItemsList = gson.fromJson(reader, LastConnectionItem[].class);

        } catch (IOException e) {
            logger.error("file not found - when the app closed, new file will created");
        }

        int count = 0;
        for (LastConnectionItem lastConnectionItem : lastConnectionItemsList) {
            if (count++ < Utils.MAX_HISTORY_CONNECT_LIST) {
                lastConnectionItems.add(lastConnectionItem);
            }
        }
    }

    public ObservableList<LastConnectionItem> getLastConnectionItems() {
        return lastConnectionItems;
    }
}
