package com.kerernor.autoconnect.model;

import com.google.gson.Gson;
import com.kerernor.autoconnect.util.KorCommon;
import com.kerernor.autoconnect.util.KorTypes;
import com.kerernor.autoconnect.util.ThreadManger;
import com.kerernor.autoconnect.util.Utils;
import com.kerernor.autoconnect.view.popups.AlertPopupController;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import java.util.Collections;
import java.util.List;

public class ComputerData {

    private final Logger logger = Logger.getLogger(ComputerData.class);
    private static final ComputerData instance = new ComputerData();
    private ObservableList<Computer> computersList; // use observable for binding data
    private ObservableList<Computer> computersListBackup; // use observable for binding data
    private final SimpleIntegerProperty stationCounterItems = new SimpleIntegerProperty(0);
    private final SimpleIntegerProperty rcgwCounterItems = new SimpleIntegerProperty(0);
    private final SimpleIntegerProperty otherCounterItems = new SimpleIntegerProperty(0);
    private final BooleanProperty isComputerListHasChanged = new SimpleBooleanProperty(false);
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

    public int getOtherCounterItems() {
        return otherCounterItems.get();
    }

    public SimpleIntegerProperty getOtherCounterItemsProperty() {
        return otherCounterItems;
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
        isComputerListHasChanged();
    }

    public void add(Computer newComputer) {
        logger.trace("add new computer - " + newComputer.getName());
        computersList.add(newComputer);
        updateCounters(newComputer, 1);
        isComputerListHasChanged();
    }

    public void update(Computer updatedComputer) {
        logger.trace("update computer - " + updatedComputer.getName());
        for (int i = 0; i < computersList.size(); i++) {
            if (computersList.get(i).getId().equals(updatedComputer.getId())) {
                computersList.set(i, updatedComputer.clone());
                calcCounters();
                break;
            }
        }

        isComputerListHasChanged();
    }

    public void swapRows(int currentIndex, int currentIndexMinusPlus) {
        Collections.swap(this.computersList, currentIndex, currentIndexMinusPlus);
        isComputerListHasChanged();
    }

    public void loadData() {
        logger.trace("ComputerData.loadData");
        computersList = FXCollections.observableArrayList(); // FXCollection is for better performance
        computersListBackup = FXCollections.observableArrayList();
        computerListSize = Bindings.size(computersList);
        isComputerListHasChanged.set(false);
        Computer[] computers = {};
        try (Reader reader = new InputStreamReader(new FileInputStream(Utils.COMPUTER_DATA), StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
            computers = gson.fromJson(reader, Computer[].class);
        } catch (IOException e) {
            logger.error("file not found - when the app closed, new file will created");
        }

        for (Computer computer : computers) {
            updateCounters(computer, 1);
            computersList.add(computer);
            computersListBackup.add(computer);
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
        if (computer.getComputerType() == KorTypes.ComputerType.RCGW) {
            int oldValue = rcgwCounterItems.get();
            rcgwCounterItems.set(oldValue + value);
            logger.info("update RCGW: oldValue: " + oldValue + " NewValue: " + rcgwCounterItems.get());
        } else if (computer.getComputerType() == KorTypes.ComputerType.Station) {
            int oldValue = stationCounterItems.get();
            stationCounterItems.set(stationCounterItems.get() + value);
            logger.info("update Station: oldValue: " + oldValue + " NewValue: " + stationCounterItems.get());
        } else {
            int oldValue = otherCounterItems.get();
            otherCounterItems.set(otherCounterItems.get() + value);
            logger.info("update Other: oldValue: " + oldValue + " NewValue: " + otherCounterItems.get());
        }
    }

    private void calcCounters() {
        stationCounterItems.setValue(0);
        rcgwCounterItems.setValue(0);
        otherCounterItems.setValue(0);
        computersList.forEach(computer -> {
            if (computer.getComputerType() == KorTypes.ComputerType.RCGW) {
                rcgwCounterItems.set(rcgwCounterItems.get() + 1);
            } else if (computer.getComputerType() == KorTypes.ComputerType.Station) {
                stationCounterItems.set(stationCounterItems.get() + 1);
            } else {
                otherCounterItems.set(otherCounterItems.get() + 1);
            }
        });
    }

    public int getComputerIndexInListByIP(String ip) {
        int index = 0;

        for (Computer computer : computersList) {
            if (computer.getIp().equals(ip)) {
                return index;
            }
            index++;
        }

        return -1;
    }

    public boolean isIsComputerListHasChanged() {
        return isComputerListHasChanged.get();
    }

    public BooleanProperty isComputerListHasChangedProperty() {
        return isComputerListHasChanged;
    }

    private void isComputerListHasChanged() {
        isComputerListHasChanged.set(!computersList.equals(computersListBackup));
        logger.info("isComputerListHasChanged - " + isComputerListHasChanged.get());
    }

    public void saveChangesToDB() {
        logger.trace("saveChangesToDB");
        isComputerListHasChanged.set(false); // prevent from user to click again until the operation complete
        ThreadManger.getInstance().getThreadPoolExecutor().execute(() -> {
            try {
                storeData();
                Collections.copy(computersListBackup, computersList);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                throw new IOException("Erroe");
            } catch (IOException e) {
                logger.error("failed to save - ", e);
                Platform.runLater(() -> {
                    isComputerListHasChanged.set(true);
                    AlertPopupController.sendAlert(KorTypes.AlertTypes.ERROR, Utils.ERROR_WHILE_SAVE_DATA, KorCommon.getInstance().getRemoteScreenController());
                });
            }
        });
    }

}
