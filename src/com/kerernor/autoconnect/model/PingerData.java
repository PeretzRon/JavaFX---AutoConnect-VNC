package com.kerernor.autoconnect.model;

import com.google.gson.Gson;
import com.kerernor.autoconnect.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PingerData {
    private Logger logger = Logger.getLogger(PingerData.class);
    private static PingerData instance = new PingerData();
    private ObservableList<Pinger> pingerObservableList;

    public static PingerData getInstance() {
        return instance;
    }

    public ObservableList<Pinger> getPingerObservableList() {
        return pingerObservableList;
    }

    public void loadData() {
        logger.trace("PingerData.loadData");
        pingerObservableList = FXCollections.observableArrayList(); // FXCollection is for better performance
        Pinger[] pingers;
        try (Reader reader = new InputStreamReader(new FileInputStream(Utils.PINGER_DATA), StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
            pingers = gson.fromJson(reader, Pinger[].class);
            pingerObservableList.addAll(Arrays.asList(pingers));
        } catch (IOException e) {
            logger.error("file not found - when the app closed, new file will created");
        }
    }

    public void storeData() throws IOException {
        logger.trace("storeData");
        Path path = Paths.get(Utils.PINGER_DATA);
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            List<Pinger> pingerList = new ArrayList<>(pingerObservableList);
            Gson gson = new Gson();
            String data = gson.toJson(pingerList);
            bw.write(data);
        }
    }

    public void add(Pinger pinger) {
        pingerObservableList.add(pinger);
    }

    public void remove(Pinger pingerToDelete) {
        pingerObservableList.remove(pingerToDelete);
    }

    public ObservableList<PingerItem> getListOfPingItemByName(String name) {
        ObservableList<PingerItem> list = FXCollections.observableArrayList();
        pingerObservableList.forEach(pinger -> {
            if (pinger.getName().equals(name)) {
                list.addAll(pinger.getData());
            }
        });
        return list;
    }
}
