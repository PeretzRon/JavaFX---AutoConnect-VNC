package com.kerernor.autoconnect.models;

import com.google.gson.Gson;
import com.kerernor.autoconnect.controllers.ItemController;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class ComputerData {

    private static final ComputerData instance = new ComputerData();
    private static final String filePath = "data.json";
    private ObservableList<ComputerNode> computersList; // use observable for binding data
    private final SimpleIntegerProperty stationCounterItems = new SimpleIntegerProperty(0);
    private final SimpleIntegerProperty rcgwCounterItems = new SimpleIntegerProperty(0);

    public static ComputerData getInstance() {
        return instance;
    }

    public IntegerProperty getStationsCounterItems() {
        return stationCounterItems;
    }

    public IntegerProperty getRcgwCounterItems() {
        return rcgwCounterItems;
    }

    public void remove(String id) {
        int indexToRemove = IntStream.range(0, computersList.size())
                .filter(i -> Objects.nonNull(computersList.get(i)))
                .filter(i -> id.equals(computersList.get(i).getItem().getIp()))
                .findFirst()
                .orElse(-1);
        updateCounters(computersList.get(indexToRemove).getItem(), -1);
        computersList.remove(indexToRemove);
    }

    public void add(Computer newComputer) throws IOException {
        FXMLLoader fxmlLoader = createNode(newComputer);
        setDetailsInFXML(fxmlLoader, newComputer);
        updateCounters(newComputer, 1);
    }

    public ObservableList<ComputerNode> getComputersList() {
        return computersList;
    }

    public void loadData() throws IOException {

        computersList = FXCollections.observableArrayList(); // FXCollection is for better performance
        Computer[] computers;
        try (Reader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            computers = gson.fromJson(reader, Computer[].class);
        }

        for (Computer computer : computers) {
            updateCounters(computer, 1);
            FXMLLoader fxmlLoader = createNode(computer);
            setDetailsInFXML(fxmlLoader, computer);
        }
    }

    public void storeData() throws IOException {
        Path path = Paths.get(filePath);
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            List<Computer> computerList = new ArrayList<>();
            computersList.forEach(computerNode -> {
                computerList.add(computerNode.getItem());
            });

            Gson gson = new Gson();
            String data = gson.toJson(computerList);
            bw.write(data);
        }
    }

    private void setDetailsInFXML(FXMLLoader fxml, Computer computer) {
        ItemController itemController = fxml.getController();
        itemController.getLocation().setText(computer.getItemLocation());
        itemController.getIp().setText(computer.getIp());
        itemController.getName().setText(computer.getName());
        switch (computer.getComputerType()) {
            case RCGW:
                itemController.getComputerType().setImage(new Image("images/antenna.png"));
                break;
            case Station:
                itemController.getComputerType().setImage(new Image("images/stationKO.png"));
                break;
        }
    }

    private FXMLLoader createNode(Computer computer) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("../fxml/Item.fxml"));
        Node node = fxmlLoader.load();
        node.setId(computer.getIp());
        ComputerNode computerNode = new ComputerNode(computer, node);
        computersList.add(computerNode);
        return fxmlLoader;
    }

    private void updateCounters(Computer computer, int value) {
        if (computer.getComputerType() == eComputerType.RCGW) {
            rcgwCounterItems.set(rcgwCounterItems.get() + value);
        } else {
            stationCounterItems.set(stationCounterItems.get() + value);
        }
    }

}
