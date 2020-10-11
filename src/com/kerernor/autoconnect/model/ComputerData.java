package com.kerernor.autoconnect.model;

import com.google.gson.Gson;
import com.kerernor.autoconnect.util.Utils;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ComputerData {

    private Logger logger = Logger.getLogger(ComputerData.class);
    private static final ComputerData instance = new ComputerData();
    private ObservableList<Computer> computersList; // use observable for binding data
    private final SimpleIntegerProperty stationCounterItems = new SimpleIntegerProperty(0);
    private final SimpleIntegerProperty rcgwCounterItems = new SimpleIntegerProperty(0);
    private IntegerBinding computerListSize;

    public static ComputerData getInstance() {
        return instance;
    }

    public IntegerProperty getStationsCounterItems() {
        return stationCounterItems;
    }

    public IntegerProperty getRcgwCounterItems() {
        return rcgwCounterItems;
    }

    public ObservableList<Computer> getComputersList() {
        return computersList;
    }

    public IntegerBinding computerListSizeProperty() {
        return computerListSize;
    }

    public void remove(Computer computerToDelete) {
        logger.trace("delete computer item - " + computerToDelete.getName());
        computersList.remove(computerToDelete);
        updateCounters(computerToDelete, -1);
    }

    public void add(Computer newComputer) {
        logger.trace("add new computer - " + newComputer.getName());
        computersList.add(newComputer);
        updateCounters(newComputer, 1);
    }

    public void update(Computer updatedComputer) {
        logger.trace("update computer - " + updatedComputer.getName());
        for (int i = 0; i < computersList.size(); i++) {
            if (computersList.get(i).getId().equals(updatedComputer.getId())) {
                computersList.set(i, updatedComputer);
                calcCounters();
                break;
            }
        }
    }

    public void loadData() throws IOException {
        logger.trace("ComputerData.loadData");
        computersList = FXCollections.observableArrayList(); // FXCollection is for better performance
        computerListSize = Bindings.size(computersList);
        Computer[] computers;
        try (Reader reader = new InputStreamReader(new FileInputStream(Utils.COMPUTER_DATA), StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
            computers = gson.fromJson(reader, Computer[].class);
        }

        for (Computer computer : computers) {
            updateCounters(computer, 1);
            computersList.add(computer);
        }
    }

    public void storeData() throws IOException {
        logger.trace("storeData");
        Path path = Paths.get(Utils.COMPUTER_DATA);
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            List<Computer> computerListToSave = new ArrayList<>(computersList);
            Gson gson = new Gson();
            String data = gson.toJson(computerListToSave);
            bw.write(data);
        }
    }

    private void updateCounters(Computer computer, int value) {
        logger.trace("updateCounters");
        if (computer.getComputerType() == eComputerType.RCGW) {
            int oldValue = rcgwCounterItems.get();
            rcgwCounterItems.set(oldValue + value);
            logger.info("update RCGW: oldValue: " + oldValue + " NewValue: " + rcgwCounterItems.get());
        } else {
            int oldValue = stationCounterItems.get();
            stationCounterItems.set(stationCounterItems.get() + value);
            logger.info("update RCGW: oldValue: " + oldValue + " NewValue: " + stationCounterItems.get());

        }
    }

    private void calcCounters() {
        stationCounterItems.setValue(0);
        rcgwCounterItems.setValue(0);
        computersList.forEach(computer -> {
            if (computer.getComputerType() == eComputerType.RCGW) {
                rcgwCounterItems.set(rcgwCounterItems.get() + 1);
            } else {
                stationCounterItems.set(stationCounterItems.get() + 1);
            }
        });
    }


}
