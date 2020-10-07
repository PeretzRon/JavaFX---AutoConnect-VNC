package com.kerernor.autoconnect.model;

import com.google.gson.Gson;
import com.kerernor.autoconnect.util.Utils;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
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

    public void loadData() throws IOException {
        logger.trace("PingerData.loadData");
        pingerObservableList = FXCollections.observableArrayList(); // FXCollection is for better performance
        Pinger[] pingers;
        try (Reader reader = new FileReader(Utils.PINGER_DATA)) {
            Gson gson = new Gson();
            pingers = gson.fromJson(reader, Pinger[].class);
            pingerObservableList.addAll(Arrays.asList(pingers));
        }
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
