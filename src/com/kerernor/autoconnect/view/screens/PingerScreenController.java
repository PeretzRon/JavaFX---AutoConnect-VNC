package com.kerernor.autoconnect.view.screens;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.Pinger;
import com.kerernor.autoconnect.model.PingerData;
import com.kerernor.autoconnect.model.PingerItem;
import com.kerernor.autoconnect.util.KorCommon;
import com.kerernor.autoconnect.util.KorEvents;
import com.kerernor.autoconnect.util.ThreadManger;
import com.kerernor.autoconnect.util.Utils;
import com.kerernor.autoconnect.view.PingGroupItemController;
import com.kerernor.autoconnect.view.PingListGroupController;
import com.kerernor.autoconnect.view.components.JSearchableTextFlowController;
import com.kerernor.autoconnect.view.popups.AddEditPingerItemsController;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class PingerScreenController extends Pane implements IDisplayable, ISearchTextFlow {

    @FXML
    private Pane pnlSetting;
    @FXML
    private FlowPane flowPaneGroupPinger;
    @FXML
    private ScrollPane pingItemsScrollPane;
    @FXML
    private PingListGroupController pingListGroupController;
    @FXML
    private ProgressBar totalProgress;
    @FXML
    private Label totalProgressLabel;
    @FXML
    private TextField filterPingerGroup;
    @FXML
    private Button checkPing;
    @FXML
    private Button addPingerItem;
    @FXML
    private Label selectedPingGroupName;
    @FXML
    private Label noResultLabel;

    private Logger logger = LogManager.getLogger(PingerScreenController.class);
    private static PingerScreenController instance = null;
    private final BooleanProperty isRunPingerButtonDisabled = new SimpleBooleanProperty(true);
    private final AtomicInteger passPing = new AtomicInteger(0);
    private boolean isCheckPingRunning = false;
    private final List<PingGroupItemController> pingGroupItemControllerList = new ArrayList<>();
    private final Set<JSearchableTextFlowController> activeSearchableTextFlowMap = ConcurrentHashMap.newKeySet();
    private ScheduledFuture timer;


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
        initController();
        addEventsHandlerForFlowPane();
        filterTextFieldListener();
        noResultLabelInitAndAddListener();
    }

    private void initController() {
        checkPing.disableProperty().bind(isRunPingerButtonDisabled);
        totalProgressLabel.setText("");
        selectedPingGroupName.setText("");
        noResultLabel.toBack();
        createPingerGroups("");
    }

    private void addEventsHandlerForFlowPane() {
        flowPaneGroupPinger.addEventHandler(KorEvents.PingerEvent.UPDATE_PINGER_ITEM, event -> {
            logger.trace("KorEvents.PingerEvent.UPDATE_PINGER_ITEMS");
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
    }

    private void filterTextFieldListener() {
        filterPingerGroup.setOnKeyReleased(keyEvent -> {
            String input = filterPingerGroup.getText().toLowerCase();
            Utils.setTextFieldOrientationByDetectLanguage(input, filterPingerGroup, true); // change direction if needed
            String inputWithoutLowerCase = filterPingerGroup.getText();
            if (Utils.IS_MARK_SEARCH_ACTIVE) {
                stopTimer();
                timer = ThreadManger.getInstance().getScheduledThreadPool().schedule(() -> {
                    Utils.updateStyleOnText(input, inputWithoutLowerCase, KorCommon.getInstance().getPingerScreenController());
                }, 100, TimeUnit.MILLISECONDS);
            }

            createPingerGroups(input);
        });
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel(true);
            timer = null;
        }
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

    private void noResultLabelInitAndAddListener() {
        if (PingerData.getInstance().getPingerObservableList().size() == 0) {
            noResultLabel.toFront();
        } else {
            noResultLabel.toBack();
        }

        flowPaneGroupPinger.getChildren().addListener((ListChangeListener<? super Node>) c -> {
            if (c.getList().size() == 0) {
                noResultLabel.toFront();
            } else {
                noResultLabel.toBack();
            }
        });
    }

    @FXML
    public void addPingerItemHandler() {
        logger.trace("addPingerItemHandler");
        AddEditPingerItemsController addEditPingerItemsController = new AddEditPingerItemsController(pnlSetting, null, false);
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
        totalProgressLabel.setText(Utils.ZERO_SLASH + listSizeOfCurrentSelected);
        pingListGroupController.getListToSendPing().forEach(pingerItem -> ThreadManger.getInstance().getThreadPoolExecutor().execute(() -> {
            sendPing(pingerItem, progress, buffer, listSizeOfCurrentSelected);
        }));
    }

    private void refreshPingerItemWhenUpdated(int listSize) {
        logger.trace("refreshPingerItemWhenUpdated");
        selectedPingGroupName.setText("");
        createPingerGroups(filterPingerGroup.getText());
        pingListGroupController.getPingerListView().getChildren().clear();
    }

    private void resetCounterAndProgressBarForPingerScreen() {
        logger.trace("resetCounterAndProgressBarForPingerScreen");
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
        isRunPingerButtonDisabled.set(true);
        PingerData.getInstance().getPingerObservableList()
                .stream()
                .filter(pinger -> pinger.getName().toLowerCase().contains(filterText.toLowerCase()))
                .forEach(pingerGroup -> {
                    PingGroupItemController item = new PingGroupItemController(pingerGroup, pnlSetting);
                    pingGroupItemControllerList.add(item);
                    item.getMainPane().setOnMousePressed(this::onPingGroupItemMousePressed);
                    flowPaneGroupPinger.getChildren().add(item);
                });
    }

    private void onPingGroupItemMousePressed(MouseEvent event) {
        event.consume();
        PingGroupItemController item = (PingGroupItemController) event.getSource();
        if (item != null) {
            logger.trace("selected item: " + item.getPingerItem().getName());
            ObservableList<PingerItem> pingerList = PingerData.getInstance().getListOfPingItemByName(item.getPingerItem().getName());
            pingListGroupController.loadList(pingerList);
            pingListGroupController.resetProgressBar();
            clearAndUpdateValues(item);
        }
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
        selectedPingGroupName.setText(item.getPingerItem().getName());
        totalProgress.setProgress(0);
        totalProgress.setVisible(false);
    }

    @Override
    public void showPane() {
        logger.trace("showPane");
        this.setVisible(true);
        this.setStyle(Utils.SCREEN_BACKGROUND_COLOR);
        this.toFront();
    }

    @Override
    public Set<JSearchableTextFlowController> getActiveSearchableTextFlowMap() {
        return activeSearchableTextFlowMap;
    }
}
