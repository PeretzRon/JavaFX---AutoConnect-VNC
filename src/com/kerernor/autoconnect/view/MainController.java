package com.kerernor.autoconnect.view;

import com.kerernor.autoconnect.model.*;
import com.kerernor.autoconnect.script.VNCRemote;
import com.kerernor.autoconnect.util.KorEvents;
import com.kerernor.autoconnect.util.ThreadManger;
import com.kerernor.autoconnect.util.Utils;
import com.kerernor.autoconnect.view.popups.AddEditComputerPopup;
import com.kerernor.autoconnect.view.popups.AddEditPingerItemsController;
import com.sun.javafx.robot.FXRobot;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
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
    private Button checkPing;

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
    private Button btnPingerScreen;

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

    private static MainController instance = new MainController();
    private final Logger logger = Logger.getLogger(MainController.class);
    private boolean isViewOnly = false;
    private final AtomicInteger passPing = new AtomicInteger(0);
    private boolean isCheckPingRunning = false;
    private final List<PingGroupItemController> pingGroupItemControllerList = new ArrayList<>();
    private BooleanProperty isRunPingerButtonDisabled = new SimpleBooleanProperty(true);

    public static MainController getInstance() {
        return instance;
    }

    public void initialize() {
        logger.trace("MainController.initialize");
        toggleGroupPinger = new ToggleGroup();
        totalProgressLabel.setText("");
        btnRemoteScreen.getStyleClass().add("selected-menu-item");
        pnlOverview.toFront();
        pnlSetting.setVisible(false);
        FilteredList<Computer> computerFilteredList = new FilteredList<>(ComputerData.getInstance().getComputersList(), computer -> true);
        computerListController.setPaneBehind(this.pnlOverview);
        computerListController.loadList(computerFilteredList);
        checkPing.disableProperty().bind(isRunPingerButtonDisabled);
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

        flowPaneGroupPinger.addEventHandler(KorEvents.PingerEvent.UPDATE_PINGER_ITEM, event -> {
            pingListGroupController.getPingerListView().getChildren().clear();
            ObservableList<PingerItem> pingerList = PingerData.getInstance().getListOfPingItemByName(event.getName());
            pingListGroupController.loadList(pingerList);
            pingListGroupController.resetProgressBar();
        });

        flowPaneGroupPinger.addEventHandler(KorEvents.PingerEvent.UPDATE_PINGER_NAME, event -> {
            refreshPingerItemWhenUpdated(event.getListSize());
        });


        PingerData.getInstance().getPingerObservableList().addListener((ListChangeListener<? super Pinger>) c -> {
            refreshPingerItemWhenUpdated(c.getList().size());
        });
    }

    private void refreshPingerItemWhenUpdated(int listSize) {
        createPingerGroups(filterPingerGroup.getText());
        pingListGroupController.getPingerListView().getChildren().clear();
        if (listSize == 0) {
            isRunPingerButtonDisabled.set(true);
        }
    }

    public void createPingerGroups(String filterText) {
        flowPaneGroupPinger.getChildren().clear();
        PingerData.getInstance().getPingerObservableList()
                .stream()
                .filter(pinger -> pinger.getName().toLowerCase().contains(filterText.toLowerCase()))
                .forEach(pingerGroup -> {
                    PingGroupItemController item = new PingGroupItemController(pingerGroup, pnlSetting);
                    pingGroupItemControllerList.add(item);
                    item.getMainPane().setOnMouseClicked(event -> {
                        isRunPingerButtonDisabled.set(false);
                        isCheckPingRunning = false;
                        pingItemsScrollPane.setVvalue(0);
                        selectItemStyle(item);
                        passPing.set(0);
                        totalProgressLabel.setText("");
                        totalProgress.setProgress(0);
                        totalProgress.setVisible(false);
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
            btnRemoteScreen.getStyleClass().add("selected-menu-item");
            btnPingerScreen.getStyleClass().remove("selected-menu-item");
            pnlOverview.toFront();
        }
        if (actionEvent.getSource() == btnPingerScreen) {
            pnlSetting.setVisible(true);
            pnlSetting.setStyle("-fx-background-color : #02050A");
            btnPingerScreen.getStyleClass().add("selected-menu-item");
            btnRemoteScreen.getStyleClass().remove("selected-menu-item");
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
        totalProgress.setVisible(true);
        passPing.set(0);
    }

    private void sendPing(PingerItem pingerItem, AtomicReference<Double> progress, double buffer, int listSize) {
        logger.info("Send Ping to: " + pingerItem.getIpAddress());
        if (Utils.isValidateIpAddress(pingerItem.getIpAddress())) {
            try {
                InetAddress ip = InetAddress.getByName(pingerItem.getIpAddress());
                if (ip != null && ip.isReachable(3000) && isCheckPingRunning) {
                    Platform.runLater(() -> pingerItem.setProgressValue(102));
                    passPing.addAndGet(1);
                } else if (isCheckPingRunning) {
                    Platform.runLater(() -> pingerItem.setProgressValue(100));
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (isCheckPingRunning) {
                    Platform.runLater(() -> pingerItem.setProgressValue(100));
                }
            }
        } else {
            if (isCheckPingRunning) {
                Platform.runLater(() -> pingerItem.setProgressValue(100));
            }
        }

        if (isCheckPingRunning) {
            progress.updateAndGet(v -> v + buffer);
            Platform.runLater(() -> {
                totalProgress.setProgress(progress.get());
                totalProgressLabel.setText(String.format("%s/%s", passPing.get(), listSize));
            });
        }
    }

    public void addPingerItemHandler() {
        logger.trace("addPingerItemHandler");
        AddEditPingerItemsController addEditPingerItemsController = new AddEditPingerItemsController(pnlSetting, null, false);
        addEditPingerItemsController.show();
    }
}