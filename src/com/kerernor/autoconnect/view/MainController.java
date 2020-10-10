package com.kerernor.autoconnect.view;

import com.kerernor.autoconnect.model.*;
import com.kerernor.autoconnect.script.VNCRemote;
import com.kerernor.autoconnect.util.KorEvents;
import com.kerernor.autoconnect.util.ThreadManger;
import com.kerernor.autoconnect.util.Utils;
import com.kerernor.autoconnect.view.popups.AddEditComputerPopup;
import com.kerernor.autoconnect.view.popups.AddEditPingerItemsController;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class MainController extends AnchorPane {

    @FXML
    private ScrollPane pingItemsScrollPane;

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
    FlowPane flowPaneGroupPinger;

    @FXML
    private Label totalProgressLabel;

    @FXML
    private ToggleGroup toggleGroupPinger;

    @FXML
    private TextField filterPingerGroup;

    private Logger logger = Logger.getLogger(MainController.class);
    private boolean isViewOnly = false;
    private AtomicInteger passPing = new AtomicInteger(0);
    private boolean isCheckPingRunning = false;
    private List<PingGroupItemController> pingGroupItemControllerList = new ArrayList<>();

//    public void initialize() {
////       RemoteScreenController remoteScreenController = new RemoteScreenController();
//    }


    public void initialize() {
        logger.trace("MainController.initialize");
        toggleGroupPinger = new ToggleGroup();
        totalProgressLabel.setText("");
        pnlOverview.toFront();
        pnlSetting.setVisible(false);
        FilteredList<Computer> computerFilteredList = new FilteredList<>(ComputerData.getInstance().getComputersList(), computer -> true);
        computerListController.setPaneBehind(this.pnlOverview);
        computerListController.loadList(computerFilteredList);
        updateCounters();

        filterPingerGroup.setOnKeyReleased(keyEvent -> {
            String input = filterPingerGroup.getText();
            createPingerGroups(input);

        });

        createPingerGroups("");


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

    private void createPingerGroups(String filterText) {
        flowPaneGroupPinger.getChildren().clear();
        PingerData.getInstance().getPingerObservableList()
                .stream()
                .filter(pinger -> pinger.getName().toLowerCase().contains(filterText.toLowerCase()))
                .forEach(pingerGroup -> {
                    PingGroupItemController item = new PingGroupItemController(pingerGroup);
                    pingGroupItemControllerList.add(item);
                    item.getMainPane().setOnMouseClicked(event -> {
                        isCheckPingRunning = false;
                        pingItemsScrollPane.setVvalue(0);
                        selectItemStyle(item);
                        passPing.set(0);
                        totalProgressLabel.setText("");
                        totalProgress.setProgress(0);
                        toggleGroupPinger.getSelectedToggle();
                        ObservableList<PingerItem> pingerList = PingerData.getInstance().getListOfPingItemByName(item.getName().getText());
                        pingListGroupController.loadList(pingerList);
                        pingListGroupController.resetProgressBar();
                    });
                    flowPaneGroupPinger.getChildren().add(item);
                });
    }

    private void selectItemStyle(PingGroupItemController item) {
        pingGroupItemControllerList.forEach(pingGroupItemController ->
                pingGroupItemController.getMainPane().getStyleClass().remove(Utils.PINGER_GROUP_ITEM_SELECTED));
        item.getMainPane().getStyleClass().add(Utils.PINGER_GROUP_ITEM_SELECTED);
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
        isCheckPingRunning = true;
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
        logger.info("Send Ping to: " + pingerItem.getIpAddress());
        try {
            InetAddress ip = InetAddress.getByName(pingerItem.getIpAddress());
            if (ip != null && ip.isReachable(3000) && isCheckPingRunning) {
                Platform.runLater(() -> pingerItem.setProgressValue(102));
                passPing.addAndGet(1);
            } else if (isCheckPingRunning) {
                Platform.runLater(() -> pingerItem.setProgressValue(100));
            }
            if (isCheckPingRunning) {
                progress.updateAndGet(v -> v + buffer);
                Platform.runLater(() -> {
                    totalProgress.setProgress(progress.get());
                    totalProgressLabel.setText(String.format("%s/%s", passPing.get(), listSize));
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (isCheckPingRunning) {
                Platform.runLater(() -> pingerItem.setProgressValue(100));
            }
        }
    }

    public void addPingerItemHandler() {
        logger.trace("addPingerItemHandler");
        AddEditPingerItemsController addEditPingerItemsController = new AddEditPingerItemsController(pnlSetting);
        addEditPingerItemsController.show();
    }
}