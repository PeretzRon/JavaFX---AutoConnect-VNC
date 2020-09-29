package com.kerernor.autoconnect.view;

import com.jfoenix.controls.JFXButton;
import com.kerernor.autoconnect.model.*;
import com.kerernor.autoconnect.script.VNCRemote;
import com.kerernor.autoconnect.util.KorEvents;
import com.kerernor.autoconnect.util.ThreadManger;
import com.kerernor.autoconnect.view.popups.AddEditComputerPopup;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class MainController extends AnchorPane {

    @FXML
    private ProgressBar totalProgress;

    @FXML
    private PingListGroupController pingListGroupController;

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
    private Button quickConnectBtn;

    @FXML
    private CheckBox viewOnlyCheckBox;

    @FXML
    private ListView<String> nameGroupPingerListView;

    @FXML
    private Label totalProgressLabel;

    private ObservableList<String> pingerData;

    private boolean isViewOnly = false;
    private AtomicInteger passPing = new AtomicInteger(0);

//    public void initialize() {
////       RemoteScreenController remoteScreenController = new RemoteScreenController();
//    }


    public void initialize() {
        totalProgressLabel.setText("");
        pingerData = FXCollections.observableArrayList();
        pnlOverview.toFront();
        pnlSetting.setVisible(false);
        FilteredList<Computer> computerFilteredList = new FilteredList<>(ComputerData.getInstance().getComputersList(), computer -> true);
        computerListController.setPaneBehind(this.pnlOverview);
        computerListController.loadList(computerFilteredList);
        updateCounters();

        PingerData.getInstance().getPingerObservableList().forEach(pinger -> {
            pingerData.add(pinger.getName());
        });

        nameGroupPingerListView.setItems(pingerData);
        nameGroupPingerListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                passPing.set(0);
                totalProgressLabel.setText("");
                totalProgress.setProgress(0);
                ObservableList<PingerItem> pingerList = PingerData.getInstance().getListOfPingItemByName(newValue);
                pingListGroupController.loadList(pingerList);
                pingListGroupController.resetProgressBar();
            }
        });

        Platform.runLater(() -> quickConnectTextField.requestFocus());
        quickConnectTextField.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                connectToVNC(quickConnectTextField.getText(), mainPane);
            }
        });

        quickConnectBtn.setOnAction(actionEvent -> {
            connectToVNC(quickConnectTextField.getText(), mainPane);
        });

        searchAreaController.getSearch().setOnKeyReleased(keyEvent -> {
            String input = searchAreaController.getSearch().getText();

            computerFilteredList.setPredicate(input.isEmpty() ? computer -> true :
                    computer -> computer.getIp().contains(input) ||
                            computer.getName().contains(input) ||
                            computer.getItemLocation().contains(input));
            computerListController.scrollTo(0);
        });

        // TODO: delete
        computerListController.addEventHandler(KorEvents.SearchComputerEvent.SEARCH_COMPUTER_EVENT, event -> {
//            System.out.println(event.getText());
        });

        computerListController.addEventHandler(KorEvents.ConnectVNCEvent.CONNECT_VNC_EVENT_EVENT, event -> {
            connectToVNC(event.getIpAddress(), event.getBehindParent());
        });


        viewOnlyCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            isViewOnly = newValue;
        });

    }

    public void handleClicks(ActionEvent actionEvent) {
        if (actionEvent.getSource() == btnRemoteScreen) {
            pnlOverview.setStyle("-fx-background-color : #02030A");
            pnlOverview.toFront();
        }
        if (actionEvent.getSource() == btnSettingsScreen) {
            pnlSetting.setVisible(true);
            pnlSetting.setStyle("-fx-background-color : #02050A");
            pnlSetting.toFront();
        }

        if (actionEvent.getSource() == btnExitApp) {
            Platform.exit();
        }
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

    private void connectToVNC(String ip, Parent paneBehind) {
        VNCRemote.connect(ip, paneBehind, isViewOnly);
    }

    public void checkPingHandler() {
        int listSizeOfCurrentSelected = pingListGroupController.getListToSendPing().size();
        AtomicReference<Double> progress = new AtomicReference<>((double) 0);
        double buffer = 1 / (double) listSizeOfCurrentSelected;

        resetCounterAndProgressBarForPingerScreen();
        totalProgressLabel.setText("0/" + listSizeOfCurrentSelected);
        pingListGroupController.getListToSendPing().forEach(pingerItem -> {
            ThreadManger.getInstance().getThreadPoolExecutor().execute(() -> {
                sendPing(pingerItem, progress, buffer, listSizeOfCurrentSelected);
            });
        });
    }

    private void resetCounterAndProgressBarForPingerScreen() {
        pingListGroupController.resetProgressBar();
        totalProgress.setProgress(0);
        passPing.set(0);
    }

    private void sendPing(PingerItem pingerItem, AtomicReference<Double> progress, double buffer, int listSize) {
        try {
            InetAddress ip = InetAddress.getByName(pingerItem.getIpAddress());
            if (ip != null && ip.isReachable(3000)) {
                Platform.runLater(() -> pingerItem.setProgressValue(102));
                passPing.addAndGet(1);
            } else {
                Platform.runLater(() -> pingerItem.setProgressValue(100));
            }
            progress.updateAndGet(v -> v + buffer);
            Platform.runLater(() -> {
                totalProgress.setProgress(progress.get());
                totalProgressLabel.setText(String.format("%s/%s", passPing.get(), listSize));
            });
        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> pingerItem.setProgressValue(100));
        }
    }


}