package com.kerernor.autoconnect.view;

import com.jfoenix.controls.JFXButton;
import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.Computer;
import com.kerernor.autoconnect.model.ComputerData;
import com.kerernor.autoconnect.script.VNCRemote;
import com.kerernor.autoconnect.util.KorEvents;
import com.kerernor.autoconnect.util.Utils;
import com.kerernor.autoconnect.view.popups.AddEditComputerPopup;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class RemoteScreenController {

    @FXML
    private ComputerListController computerListController;

    @FXML
    private SearchAreaController searchAreaController;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Button btnRemoteScreen;

    @FXML
    private Button btnExitApp;

    @FXML
    private Button btnSettingsScreen;

    @FXML
    private Pane pnlSetting;

    @FXML
    private Pane pnlOverview;

    @FXML
    private Pane pnlMenus;

    @FXML
    private Label totalComputers;

    @FXML
    private Label stationCounter;

    @FXML
    private Label rcgwCounter;

    @FXML
    private TextField quickConnectTextField;

    @FXML
    private JFXButton quickConnectBtn;

    public RemoteScreenController() {
        loadView();
    }

    private void loadView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Utils.REMOTE_SCREEN_VIEW));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        FilteredList<Computer> computerFilteredList = new FilteredList<>(ComputerData.getInstance().getComputersList(), computer -> true);
        computerListController.setPaneBehind(this.pnlOverview);
        computerListController.loadList(computerFilteredList);
        updateCounters();

        Platform.runLater(() -> quickConnectTextField.requestFocus());

        quickConnectTextField.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                VNCRemote.connect(quickConnectTextField.getText());
            }
        });

        quickConnectBtn.setOnAction(actionEvent -> {
            VNCRemote.connect(quickConnectTextField.getText());
        });

        searchAreaController.getSearch().setOnKeyReleased(keyEvent -> {
            String input = searchAreaController.getSearch().getText();

            computerFilteredList.setPredicate(input.isEmpty() ? computer -> true :
                    computer -> computer.getIp().contains(input) ||
                            computer.getName().contains(input) ||
                            computer.getItemLocation().contains(input));
        });

        // TODO: delete
        computerListController.addEventHandler(KorEvents.SearchComputerEvent.SEARCH_COMPUTER_EVENT, event -> {
            System.out.println(event.getText());
        });
    }

    private void updateCounters() {
        totalComputers.textProperty().bind(Bindings.concat(ComputerData.getInstance().computerListSizeProperty()));
        stationCounter.textProperty().bind(Bindings.concat(ComputerData.getInstance().getStationsCounterItems()));
        rcgwCounter.textProperty().bind(Bindings.concat(ComputerData.getInstance().getRcgwCounterItems()));
    }

    public void addNewComputer() {
        AddEditComputerPopup addEditComputerPopup = new AddEditComputerPopup(pnlOverview);
        addEditComputerPopup.openPopup(false, null);
    }



}
