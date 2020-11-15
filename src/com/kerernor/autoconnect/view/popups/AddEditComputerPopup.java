package com.kerernor.autoconnect.view.popups;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.Computer;
import com.kerernor.autoconnect.model.ComputerData;
import com.kerernor.autoconnect.model.eComputerType;
import com.kerernor.autoconnect.util.Utils;
import com.kerernor.autoconnect.view.components.JTextFieldController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class AddEditComputerPopup extends BorderPane {

    @FXML
    private BorderPane mainPane;

    @FXML
    private Label addEditComputerLabel;

    @FXML
    private JTextFieldController computerIPAddress;

    @FXML
    private JTextFieldController computerName;

    @FXML
    private JTextFieldController computerLocation;

    @FXML
    private ToggleGroup computerType;

    @FXML
    private RadioButton stationRadioButton;

    @FXML
    private RadioButton rcgwRadioButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;


    private Parent paneBehind;
    private Computer computer;
    private Stage stage;
    private BooleanProperty isValidIPAddress;
    private BooleanProperty isValidLocationTextField;
    private BooleanProperty isValidNameTextField;
    private boolean isEdit;

    public AddEditComputerPopup(Parent paneBehind, boolean isEdit) {
        this.paneBehind = paneBehind;
        this.isEdit = isEdit;
        isValidLocationTextField = new SimpleBooleanProperty(true);
        isValidNameTextField = new SimpleBooleanProperty(true);
        isValidIPAddress = new SimpleBooleanProperty(true);
    }

    @FXML
    public void initialize() {
        mainPane.setOnMousePressed(e -> mainPane.requestFocus());
        if (isEdit) {
            isValidLocationTextField = new SimpleBooleanProperty(false);
            isValidNameTextField = new SimpleBooleanProperty(false);
            isValidIPAddress = new SimpleBooleanProperty(false);
        }

        saveButton.disableProperty().bind(Bindings.or(isValidIPAddress, isValidLocationTextField).or(isValidNameTextField));
        computerIPAddress.setOnKeyReleased(event -> {
            isValidIPAddress.setValue(!Utils.isValidateIpAddress(computerIPAddress.getTextField().getText()));
        });

        computerLocation.setOnKeyReleased(event -> {
            isValidLocationTextField.setValue(Utils.isNullOrEmptyString(computerLocation.getTextField().getText()));
        });

        computerName.setOnKeyReleased(event -> {
            isValidNameTextField.setValue(Utils.isNullOrEmptyString(computerName.getTextField().getText()));
        });

        initTextFields();
    }

    private void initTextFields() {
        computerIPAddress.setInitData("IP Address", 14, computerIPAddress.getPrefWidth());
        computerIPAddress.setTextFieldColor("#fff");
        computerName.setInitData("Computer Name", 14, computerName.getPrefWidth());
        computerName.setTextFieldColor("#fff");
        computerLocation.setInitData("Computer Location", 14, computerName.getPrefWidth());
        computerLocation.setTextFieldColor("#fff");
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


    public void openPopup(Computer computer) {
        // not require
        // fireEvent(new KorEvents.SearchComputerEvent(KorEvents.SearchComputerEvent.SEARCH_COMPUTER_EVENT, "Event"));

        this.computer = computer;
        Scene scene = new Scene(this.loadView());

        stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);

        if (isEdit) {
            setValues(computer);
            addEditComputerLabel.setText(Utils.TEXT_EDIT_COMPUTER_POPUP + computer.getName());
        } else {
            // new computer
            stationRadioButton.setSelected(true);
            addEditComputerLabel.setText(Utils.TEXT_ADD_NEW_COMPUTER_POPUP);
        }

        if (Utils.CONFIG_IS_POPUP_CLOSE_IF_LOSE_FOCUS_SETTING) {
            // Set out of focus closing ability
            stage.focusedProperty().addListener((observableValue, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    closeClickAction();
                }
            });
        }


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
        computerIPAddress.getTextField().setText(computer.getIp());
        computerLocation.getTextField().setText(computer.getItemLocation());
        computerName.getTextField().setText(computer.getName());
//        Platform.runLater(() -> computerIPAddress.getTextField().end());
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
        String cmpIP = computerIPAddress.getTextField().getText();
        String cmpName = computerName.getTextField().getText();
        String cmpLocation = computerLocation.getTextField().getText();
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
