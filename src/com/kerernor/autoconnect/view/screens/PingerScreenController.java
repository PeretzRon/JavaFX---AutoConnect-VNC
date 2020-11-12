package com.kerernor.autoconnect.view.screens;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.Pinger;
import com.kerernor.autoconnect.model.PingerData;
import com.kerernor.autoconnect.model.PingerItem;
import com.kerernor.autoconnect.util.KorEvents;
import com.kerernor.autoconnect.util.ThreadManger;
import com.kerernor.autoconnect.util.Utils;
import com.kerernor.autoconnect.view.PingGroupItemController;
import com.kerernor.autoconnect.view.PingListGroupController;
import com.kerernor.autoconnect.view.popups.AddEditPingerItemsController;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class PingerScreenController extends Pane implements IDisplayable {

    @FXML
    public Pane pnlSetting;
    @FXML
    public FlowPane flowPaneGroupPinger;
    @FXML
    public ScrollPane pingItemsScrollPane;
    @FXML
    public PingListGroupController pingListGroupController;
    @FXML
    public ProgressBar totalProgress;
    @FXML
    public Label totalProgressLabel;
    @FXML
    public TextField filterPingerGroup;
    @FXML
    public Button checkPing;
    @FXML
    public Button addPingerItem;
    @FXML
    private Label selectedPingGroupName;

    private Logger logger = Logger.getLogger(PingerScreenController.class);
    private static PingerScreenController instance = null;
    FXMLLoader loader = null;
    private final BooleanProperty isRunPingerButtonDisabled = new SimpleBooleanProperty(true);
    private final AtomicInteger passPing = new AtomicInteger(0);
    private boolean isCheckPingRunning = false;
    private final List<PingGroupItemController> pingGroupItemControllerList = new ArrayList<>();

    public static PingerScreenController getInstance() {
        if (instance == null) {
            instance = new PingerScreenController();
        }
        return instance;
    }

    private PingerScreenController() {
        loadView();
    }

    @FXML
    public void initialize() {
        checkPing.disableProperty().bind(isRunPingerButtonDisabled);
        totalProgressLabel.setText("");
        selectedPingGroupName.setText("");

        flowPaneGroupPinger.addEventHandler(KorEvents.PingerEvent.UPDATE_PINGER_ITEM, event -> {
            logger.trace("KorEvents.PingerEvent.UPDATE_PINGER_ITEM");
            pingListGroupController.getPingerListView().getChildren().clear();
            ObservableList<PingerItem> pingerList = PingerData.getInstance().getListOfPingItemByName(event.getName());
            pingListGroupController.loadList(pingerList);
            pingListGroupController.resetProgressBar();
        });

        flowPaneGroupPinger.addEventHandler(KorEvents.PingerEvent.UPDATE_PINGER_NAME, event -> {
            logger.trace("KorEvents.PingerEvent.UPDATE_PINGER_NAME");
            refreshPingerItemWhenUpdated(event.getListSize());
        });

        PingerData.getInstance().getPingerObservableList().addListener((ListChangeListener<? super Pinger>) c -> {
            logger.trace("ListChangeListener - Pinger");
            refreshPingerItemWhenUpdated(c.getList().size());
        });

        filterPingerGroup.setOnKeyReleased(keyEvent -> {
            String input = filterPingerGroup.getText();
            createPingerGroups(input);
        });

        createPingerGroups("");
    }

    private Pane loadView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Utils.PINGER_SCREEN));
        loader.setController(this);
        loader.setRoot(this);

        try {
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @FXML
    public void addPingerItemHandler() {
        logger.trace("addPingerItemHandler");

        AddEditPingerItemsController addEditPingerItemsController = AddEditPingerItemsController.getInstance();
        addEditPingerItemsController.setConfiguration(pnlSetting, null, false);
        addEditPingerItemsController.show();
    }

    @FXML
    public void checkPingHandler() {
        logger.trace("Start Sending Ping");
        isCheckPingRunning = true;
        int listSizeOfCurrentSelected = pingListGroupController.getListToSendPing().size();
        AtomicReference<Double> progress = new AtomicReference<>((double) 0);
        double buffer = 1 / (double) listSizeOfCurrentSelected;

        resetCounterAndProgressBarForPingerScreen();
        totalProgressLabel.setText("0/" + listSizeOfCurrentSelected);
        pingListGroupController.getListToSendPing().forEach(pingerItem -> ThreadManger.getInstance().getThreadPoolExecutor().execute(() -> {
            sendPing(pingerItem, progress, buffer, listSizeOfCurrentSelected);
        }));
    }

    private void refreshPingerItemWhenUpdated(int listSize) {
        logger.trace("refreshPingerItemWhenUpdated");
        createPingerGroups(filterPingerGroup.getText());
        pingListGroupController.getPingerListView().getChildren().clear();
        if (listSize == 0) {
            isRunPingerButtonDisabled.set(true);
        }
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

    public void createPingerGroups(String filterText) {
        flowPaneGroupPinger.getChildren().clear();
        selectedPingGroupName.setText("");
        PingerData.getInstance().getPingerObservableList()
                .stream()
                .filter(pinger -> pinger.getName().toLowerCase().contains(filterText.toLowerCase()))
                .forEach(pingerGroup -> {
                    PingGroupItemController item = new PingGroupItemController(pingerGroup, pnlSetting);
                    pingGroupItemControllerList.add(item);
                    item.getMainPane().setOnMouseClicked(event -> {
                        logger.trace("selected item: " + item.getName().getText());
                        clearAndUpdateValues(item);
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

    private void clearAndUpdateValues(PingGroupItemController item) {
        isRunPingerButtonDisabled.set(false);
        isCheckPingRunning = false;
        pingItemsScrollPane.setVvalue(0);
        selectItemStyle(item);
        passPing.set(0);
        totalProgressLabel.setText("");
        selectedPingGroupName.setText(item.getName().getText());
        totalProgress.setProgress(0);
        totalProgress.setVisible(false);
    }

    @Override
    public void showPane() {
        this.setVisible(true);
        this.setStyle("-fx-background-color : #02050A");
        this.toFront();
        logger.trace("showPane");
    }
    //TOOD: fix bug button is allow after create new group
    //TOOD: group name cut
}
