package com.kerernor.autoconnect.models;

import com.google.gson.Gson;
import com.kerernor.autoconnect.util.Utils;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ComputerData {

    private static final ComputerData instance = new ComputerData();
    private ObservableList<Computer> computersList; // use observable for binding data
    private final SimpleIntegerProperty stationCounterItems = new SimpleIntegerProperty(0);
    private final SimpleIntegerProperty rcgwCounterItems = new SimpleIntegerProperty(0);
    private IntegerBinding computerListSize;
    private Logger logger = Logger.getLogger(ComputerData.class);

    public static ComputerData getInstance() {
        return instance;
    }

    public IntegerProperty getStationsCounterItems() {
        return stationCounterItems;
    }

    public IntegerProperty getRcgwCounterItems() {
        return rcgwCounterItems;
    }

    public Number getComputerListSize() {
        return computerListSize.get();
    }

    public ObservableList<Computer> getComputersList() {
        return computersList;
    }

    public IntegerBinding computerListSizeProperty() {
        return computerListSize;
    }

    public void remove(Computer computerToDelete) {
        computersList.remove(computerToDelete);
        updateCounters(computerToDelete, -1);
    }

    public void add(Computer newComputer) throws IOException {
//        FXMLLoader fxmlLoader = createNode(newComputer);
//        setDetailsInFXML(fxmlLoader, newComputer);
//        updateCounters(newComputer, 1);
    }

    public void loadData() throws IOException {
        logger.info("load data from json");
        computersList = FXCollections.observableArrayList(); // FXCollection is for better performance
        computerListSize = Bindings.size(computersList);
        Computer[] computers;
        try (Reader reader = new FileReader(Utils.COMPUTER_DATA)) {
            Gson gson = new Gson();
            computers = gson.fromJson(reader, Computer[].class);
        }

        for (Computer computer : computers) {
            updateCounters(computer, 1);
            computersList.add(computer);
        }
    }

    public void storeData() throws IOException {
        Path path = Paths.get(Utils.COMPUTER_DATA);
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            List<Computer> computerListToSave = new ArrayList<>(computersList);
            Gson gson = new Gson();
            String data = gson.toJson(computerListToSave);
            bw.write(data);
        }
    }

    private void updateCounters(Computer computer, int value) {
        if (computer.getComputerType() == eComputerType.RCGW) {
            rcgwCounterItems.set(rcgwCounterItems.get() + value);
        } else {
            stationCounterItems.set(stationCounterItems.get() + value);
        }
    }
}
