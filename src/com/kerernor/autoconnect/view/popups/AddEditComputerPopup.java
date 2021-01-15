package com.kerernor.autoconnect.view.popups;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.Computer;
import com.kerernor.autoconnect.model.ComputerData;
import com.kerernor.autoconnect.util.KorCommon;
import com.kerernor.autoconnect.util.KorTypes;
import com.kerernor.autoconnect.util.Utils;
import com.kerernor.autoconnect.view.components.JTextFieldController;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private RadioButton otherRadioButton;

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
    private Logger logger = LogManager.getLogger(AddEditComputerPopup.class);

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

        // save changes by click Enter on keyboard
        mainPane.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (!saveButton.isDisable()) {
                    saveClickAction();
                }
            }
        });
        checkValidateOfTextFieldsOfTextFields();
        initTextFields();
        textFieldsOrientationListeners();
    }

    private void checkValidateOfTextFieldsOfTextFields() {
        computerIPAddress.setOnKeyReleased(event -> {
            isValidIPAddress.setValue(!Utils.isValidateIpAddress(computerIPAddress.getTextField().getText()));
        });

        computerLocation.setOnKeyReleased(event -> {
            isValidLocationTextField.setValue(Utils.isNullOrEmptyString(computerLocation.getTextField().getText()));
        });

        computerName.setOnKeyReleased(event -> {
            isValidNameTextField.setValue(Utils.isNullOrEmptyString(computerName.getTextField().getText()));
        });
    }

    private void initTextFields() {
        computerIPAddress.setInitData("IP Address", 20, computerIPAddress.getPrefWidth());
        computerIPAddress.setTextFieldColor("#fff");
        computerIPAddress.setFontPlaceHolderActive(14);
        computerIPAddress.setFontPlaceHolderNotActive(18);
        computerName.setInitData("Computer Name", 20, computerName.getPrefWidth());
        computerName.setTextFieldColor("#fff");
        computerName.setFontPlaceHolderActive(14);
        computerName.setFontPlaceHolderNotActive(18);
        computerLocation.setInitData("Computer Location", 20, computerName.getPrefWidth());
        computerLocation.setTextFieldColor("#fff");
        computerLocation.setFontPlaceHolderActive(14);
        computerLocation.setFontPlaceHolderNotActive(18);
    }

    private void textFieldsOrientationListeners() {
        final TextField textFieldComputerIP = computerIPAddress.getTextField();
        textFieldComputerIP.setOnKeyPressed(event -> {
            Utils.setTextFieldOrientationByDetectLanguage(
                    textFieldComputerIP.getText(), textFieldComputerIP, true);
        });

        final TextField textFieldComputerLocation = computerLocation.getTextField();
        textFieldComputerLocation.setOnKeyPressed(event -> {
            Utils.setTextFieldOrientationByDetectLanguage(
                    textFieldComputerLocation.getText(), textFieldComputerLocation, true);
        });

        final TextField textFieldComputerName = computerName.getTextField();
        textFieldComputerName.setOnKeyPressed(event -> {
            Utils.setTextFieldOrientationByDetectLanguage(
                    textFieldComputerName.getText(), textFieldComputerName, true);
        });

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

    public void show0(Computer computer) {
//        Timeline tick0 = new Timeline();
//        tick0.setCycleCount(Timeline.INDEFINITE);
//        tick0.getKeyFrames().add(
//                new KeyFrame(new Duration(8), new EventHandler<ActionEvent>() {
//                    public void handle(ActionEvent t) {
////                        Pane root = KorCommon.getInstance().getRemoteScreenController();
//                        paneBehind.setOpacity(paneBehind.getOpacity() - 0.01);
//                        if (paneBehind.getOpacity() < 0.01) {//30 divided by 0.01 equals 3000 so you take the duration and divide it be the opacity to get your transition time in milliseconds
//
//                            tick0.stop();
//                        }
//                    }
//                }));
//        tick0.play();
        show(computer);
    }


    public void show(Computer computer) {
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
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(paneBehind.getScene().getWindow());
        Utils.enableExitPopupOnEscKey(stage, callback -> closeClickAction());

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
        Utils.setTextFieldOrientationByDetectLanguage(computer.getName(), computerName.getTextField(), false);
        Utils.setTextFieldOrientationByDetectLanguage(computer.getIp(), computerIPAddress.getTextField(), false);
        Utils.setTextFieldOrientationByDetectLanguage(computer.getItemLocation(), computerLocation.getTextField(), false);

        Platform.runLater(() -> computerIPAddress.getTextField().end());
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
            case Other:
                otherRadioButton.setSelected(true);
                break;
        }
    }

    public void closeClickAction() {
//        Timeline timeline = new Timeline();;
//        mainPane.prefWidthProperty().addListener((observable, oldValue, newValue) -> {
//            stage.setWidth(newValue.doubleValue());
//        });
//        mainPane.prefHeightProperty().addListener((observable, oldValue, newValue) -> {
//            stage.setHeight(newValue.doubleValue());
//        });
//        KeyFrame key = new KeyFrame(Duration.millis(1000),
//                new KeyValue(mainPane.prefWidthProperty(), 0));
//        KeyFrame key2 = new KeyFrame(Duration.millis(1000),
//                new KeyValue(mainPane.prefHeightProperty(), 0));
//        timeline.getKeyFrames().add(key);
//        timeline.getKeyFrames().add(key2);
//        timeline.setOnFinished((ae) -> {
//            stage.close();
//            // Revert the blur effect from the pane behind
//            paneBehind.effectProperty().setValue(Utils.getEmptyEffect());
//        });
//        timeline.play();
        stage.close();
        // Revert the blur effect from the pane behind
        paneBehind.effectProperty().setValue(Utils.getEmptyEffect());

    }

    // save new computer / edit computer to list
    public void saveClickAction() {
        String cmpIP = computerIPAddress.getTextField().getText();
        String cmpName = computerName.getTextField().getText();
        String cmpLocation = computerLocation.getTextField().getText();
        RadioButton selectedRadioButton = (RadioButton) computerType.getSelectedToggle();
        KorTypes.ComputerType cmpType = Enum.valueOf(KorTypes.ComputerType.class, selectedRadioButton.getText());

        if (computer == null) {
            // create new computer
            this.computer = new Computer(cmpIP, cmpName, cmpLocation, cmpType);
            ComputerData.getInstance().add(this.computer);
        } else {
            // edit exist computer
            Computer computer = new Computer(cmpIP, cmpName, cmpLocation, cmpType, this.computer.getId());
            ComputerData.getInstance().update(computer);
        }

        closeClickAction();
    }


}
