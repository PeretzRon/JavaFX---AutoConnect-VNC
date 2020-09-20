package com.kerernor.autoconnect.view.popups;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.Computer;
import com.kerernor.autoconnect.model.ComputerData;
import com.kerernor.autoconnect.model.eComputerType;
import com.kerernor.autoconnect.util.KorEvents;
import com.kerernor.autoconnect.util.Utils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class AddEditComputerPopup extends Popup {

    @FXML
    BorderPane mainPane;

    @FXML
    private Label addEditComputerLabel;

    @FXML
    private TextField computerIPAddress;

    @FXML
    private TextField computerName;

    @FXML
    private TextField computerLocation;

    @FXML
    private ToggleGroup computerType;

    @FXML
    private RadioButton stationRadioButton;

    @FXML
    private RadioButton rcgwRadioButton;


    private Parent paneBehind;
    private Computer computer;
    private Stage stage;

    public AddEditComputerPopup(Parent paneBehind) {
        this.paneBehind = paneBehind;
    }

    @FXML
    public void initialize() {
        Platform.runLater(() ->  mainPane.requestFocus());

    }

    public BorderPane loadView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Utils.ADD_EDIT_COMPUTER_POPUP));
        loader.setController(this);

        try {
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public void openPopup(boolean isUpdate, Computer computer) {
        // TODO: - Delete
        fireEvent(new KorEvents.SearchComputerEvent(KorEvents.SearchComputerEvent.SEARCH_COMPUTER_EVENT, "Event"));

        this.computer = computer;
        Scene scene = new Scene(this.loadView());

        stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);

        // select radio button
        if (isUpdate) {
            setValues(computer);
            addEditComputerLabel.setText(Utils.TEXT_EDIT_COMPUTER_POPUP + computer.getName());
        } else {
            // new computer
            stationRadioButton.setSelected(true);
            addEditComputerLabel.setText(Utils.TEXT_ADD_NEW_COMPUTER_POPUP);
        }

        // Set out of focus closing ability
        stage.focusedProperty().addListener((observableValue, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                closeClickAction();
            }
        });

        // Prevent the window from closing in case of out of focus
        stage.initModality(Modality.NONE);
        stage.initOwner(paneBehind.getScene().getWindow());
        stage.show();

        // center stage
        Utils.centerNewStageToBehindStage(paneBehind, stage);

        // Blur the pane behind
        paneBehind.effectProperty().setValue(Utils.getBlurEffect());

    }

    private void setValues(Computer computer) {
        computerIPAddress.setText(computer.getIp());
        computerLocation.setText(computer.getItemLocation());
        computerName.setText(computer.getName());
        selectRadioButton(computer);
    }

    private void selectRadioButton(Computer computer) {
        switch (computer.getComputerType()) {
            case RCGW:
                rcgwRadioButton.setSelected(true);
                break;
            case Station:
                stationRadioButton.setSelected(true);
                break;
        }
    }

    public void closeClickAction() {
        // Revert the blur effect from the pane behind
        paneBehind.effectProperty().setValue(Utils.getEmptyEffect());


        stage.close();
    }

    // save new computer / edit computer to list
    public void saveClickAction() {
        String cmpIP = computerIPAddress.getText();
        String cmpName = computerName.getText();
        String cmpLocation = computerLocation.getText();
        RadioButton selectedRadioButton = (RadioButton) computerType.getSelectedToggle();
        eComputerType cmpType = Enum.valueOf(eComputerType.class, selectedRadioButton.getText());

        if (computer == null) {
            // create new computer
            this.computer = new Computer(cmpIP, cmpName, cmpLocation, cmpType);
            ComputerData.getInstance().add(this.computer);
        } else {
            // edit exist computer
            this.computer.setIp(cmpIP);
            this.computer.setLocation(cmpLocation);
            this.computer.setName(cmpName);
            this.computer.setComputerType(cmpType);
            ComputerData.getInstance().update(this.computer);
        }

        closeClickAction();
    }
}
