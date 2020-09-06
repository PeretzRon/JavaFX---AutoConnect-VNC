package com.kerernor.autoconnect.controllers;

import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import com.kerernor.autoconnect.models.Computer;
import com.kerernor.autoconnect.models.ComputerData;
import com.kerernor.autoconnect.models.eComputerType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class DialogController implements Initializable {


    @FXML
    private JFXTextField computerLocation;

    @FXML
    private JFXTextField computerName;

    @FXML
    private JFXTextField computerIPAddress;

    @FXML
    private BorderPane borderPaneDialog;

    @FXML
    private JFXRadioButton radioBtnRCGW;

    @FXML
    private JFXRadioButton radioBtnStation;

    @FXML
    private ToggleGroup computerType;

    public void processResults() {
        try {
            String ipAddress = computerIPAddress.getText();
            String name = computerName.getText();
            String location = computerLocation.getText();
            JFXRadioButton selectedRadioButton = (JFXRadioButton) computerType.getSelectedToggle();
            eComputerType type = Enum.valueOf(eComputerType.class, selectedRadioButton.getText());
            Computer newComputer = new Computer(ipAddress, name, location, type);
            ComputerData.getInstance().add(newComputer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        radioBtnStation.setSelected(true);
    }

    public JFXTextField getComputerLocation() {
        return computerLocation;
    }

    public JFXTextField getComputerName() {
        return computerName;
    }

    public JFXTextField getComputerIPAddress() {
        return computerIPAddress;
    }
}
