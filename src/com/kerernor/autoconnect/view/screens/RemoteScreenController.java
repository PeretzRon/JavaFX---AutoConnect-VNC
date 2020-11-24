package com.kerernor.autoconnect.view.screens;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.Computer;
import com.kerernor.autoconnect.model.ComputerData;
import com.kerernor.autoconnect.model.LastConnectionData;
import com.kerernor.autoconnect.model.LastConnectionItem;
import com.kerernor.autoconnect.script.VNCRemote;
import com.kerernor.autoconnect.util.*;
import com.kerernor.autoconnect.view.ComputerListController;
import com.kerernor.autoconnect.view.ComputerRowController;
import com.kerernor.autoconnect.view.LastConnectionsPopupController;
import com.kerernor.autoconnect.view.components.JSearchableTextFlowController;
import com.kerernor.autoconnect.view.components.JTextFieldController;
import com.kerernor.autoconnect.view.popups.AddEditComputerPopup;
import com.kerernor.autoconnect.view.popups.AlertPopupController;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class RemoteScreenController extends Pane implements IDisplayable {

    @FXML
    private Pane mainPane;
    @FXML
    private Label totalComputers;
    @FXML
    private Label stationCounter;
    @FXML
    private Label rcgwCounter;
    @FXML
    private JTextFieldController searchAreaController;
    @FXML
    private CheckBox viewOnlyCheckBox;
    @FXML
    private ComputerListController computerListController;
    @FXML
    private TextField quickConnectTextField;
    @FXML
    private Button quickConnectBtn;
    @FXML
    private LastConnectionsPopupController lastConnectionsPopupController;
    @FXML
    private ImageView openCloseHistoryImage;
    @FXML
    private Label resultLabel;
    @FXML
    private ImageView addNewComputerImage;
    @FXML
    private ImageView upRowImageView;
    @FXML
    private ImageView downRowImageView;
    @FXML
    private Button saveChanges;


    private Logger logger = Logger.getLogger(RemoteScreenController.class);
    private static RemoteScreenController instance = null;
    private final BooleanProperty isHistoryListEmpty = new SimpleBooleanProperty(true);
    private boolean isHistoryListOpen = false;
    private boolean isViewOnly = false;
    private ScheduledFuture timer;
    private boolean isAllowedToMoveRows = true;


    public static RemoteScreenController getInstance() {
        if (instance == null) {
            instance = new RemoteScreenController();
        }
        return instance;
    }

    private RemoteScreenController() {
        loadView();
    }

    private Pane loadView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Utils.REMOTE_SCREEN));
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
    public void initialize() {
        FilteredList<Computer> computerFilteredList = new FilteredList<>(ComputerData.getInstance().getComputersList(), computer -> true);
        FilteredList<LastConnectionItem> historySearchFilteredList = new FilteredList<>(LastConnectionData.getInstance().getLastConnectionItems(), pingItemsScrollPane -> true);
        computerListController.setPaneBehind(this.mainPane);
        computerListController.loadList(computerFilteredList);
        lastConnectionsPopupController.setList(historySearchFilteredList);
        isHistoryListEmpty.set(historySearchFilteredList.size() == 0);
        openCloseHistoryImage.disableProperty().bind(isHistoryListEmpty);
        saveChanges.disableProperty().bind(Bindings.not(ComputerData.getInstance().isComputerListHasChangedProperty()));
        updateCounters();

        ComputerData.getInstance().getComputersList().addListener((ListChangeListener<? super Computer>) c -> {
            logger.trace("ComputerData ListChangeListener");
            final String input = searchAreaController.getTextField().getText();
            updateStyleOnText(input, input.toLowerCase());
        });

        LastConnectionData.getInstance().getLastConnectionItems().addListener((ListChangeListener<? super LastConnectionItem>) c -> {
            if (c.getList().size() == 0) {
                lastConnectionsPopupController.hide();
                isHistoryListOpen = false;
                isHistoryListEmpty.set(true);
            } else {
                isHistoryListEmpty.set(false);
            }
        });

        quickConnectTextField.setOnKeyReleased(keyEvent -> {
            String input = quickConnectTextField.getText();

            if (keyEvent.getCode() == KeyCode.ENTER) {
                connectToVNC(input);
            }

            historySearchFilteredList.setPredicate(input.isEmpty() ? lastConnectionItem -> true :
                    lastConnectionItem -> lastConnectionItem.getIp().contains(input));

            if (historySearchFilteredList.isEmpty()) {
                lastConnectionsPopupController.hide();
            } else if (isHistoryListOpen) {
                if (!lastConnectionsPopupController.isShow()) {
                    openLastConnectionPopupController();
                    quickConnectTextField.requestFocus();
                    quickConnectTextField.selectEnd();
                }
            }
        });

        quickConnectBtn.setOnAction(actionEvent -> {
            connectToVNC(quickConnectTextField.getText());
        });

        searchAreaController.getTextField().setOnKeyReleased(keyEvent -> {
            String input = searchAreaController.getTextField().getText().toLowerCase();
            isAllowedToMoveRows = input.isEmpty(); // allow or disallow to move rows
            String inputWithoutLowerCase = searchAreaController.getTextField().getText();

            computerFilteredList.setPredicate(input.isEmpty() ? computer -> true :
                    computer -> computer.getIp().contains(input) ||
                            computer.getName().toLowerCase().contains(input) ||
                            computer.getItemLocation().toLowerCase().contains(input));
            computerListController.scrollTo(0);

            if (Utils.IS_MARK_SEARCH_ACTIVE) {
                stopTimer();
                timer = ThreadManger.getInstance().getScheduledThreadPool().schedule(() -> {
                    updateStyleOnText(input, inputWithoutLowerCase);
                }, 100, TimeUnit.MILLISECONDS);
            }
        });

//        computerListController.addEventHandler(KorEvents.SearchComputerEvent.SEARCH_COMPUTER_EVENT, event -> {
//            event.consume();
//        });

        computerListController.addEventHandler(KorEvents.ConnectVNCEvent.CONNECT_VNC_EVENT_EVENT, event -> {
            connectToVNC(event.getIpAddress());
        });

        viewOnlyCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            isViewOnly = newValue;
        });

        openCloseHistoryImage.setOnMouseClicked(event -> {
            if (lastConnectionsPopupController.isShow()) {
                lastConnectionsPopupController.hide();
                isHistoryListOpen = false;
            } else {
                openLastConnectionPopupController();
            }
        });

        lastConnectionsPopupController.addEventFilter(KorEvents.SearchHistoryConnectionEvent.SEARCH_HISTORY_CONNECTION_EVENT_EVENT_TYPE, event -> {
            event.consume();
            quickConnectTextField.setText(event.getLastConnectionItem().getIp());
            lastConnectionsPopupController.hide();
            isHistoryListOpen = false;
            quickConnectTextField.requestFocus();
            quickConnectTextField.selectEnd();
        });

        searchAreaController.setInitData("Filter list", 18, searchAreaController.getPrefWidth());
        searchAreaController.setTextFieldColor("#fff");

        mainPane.setOnMousePressed(e -> mainPane.requestFocus());

        Platform.runLater(() -> quickConnectTextField.requestFocus());

        noResultLabelInitAndAddListener();
        Utils.createTooltipListener(addNewComputerImage, Utils.NEW_ITEM, KorTypes.ShowNodeFrom.LEFT);
    }

    public void updateStyleOnText(String input, String inputWithoutLowerCase) {
        Platform.runLater(() -> {
            logger.info("Size: " + JSearchableTextFlowController.getActiveSearchableTextFlowMap().size());
            for (JSearchableTextFlowController searchableTextFlowController : JSearchableTextFlowController.getActiveSearchableTextFlowMap()) {
                if (input.isEmpty()) {
                    searchableTextFlowController.setOriginalText();
                } else {
                    searchableTextFlowController.updatedText(inputWithoutLowerCase);
                }
            }
        });
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel(true);
            timer = null;
        }
    }

    private void noResultLabelInitAndAddListener() {
        if (ComputerData.getInstance().getComputersList().size() == 0) {
            resultLabel.toFront();
        } else {
            resultLabel.toBack();
        }

        computerListController.getComputerListView().getItems().addListener((ListChangeListener<? super Computer>) c -> {
            if (c.getList().size() == 0) {
                resultLabel.toFront();
            } else {
                resultLabel.toBack();
            }
        });
    }

    private void openLastConnectionPopupController() {
        if (LastConnectionData.getInstance().getLastConnectionItems().size() > 0) {
            logger.trace("openLastConnectionPopupController");
            lastConnectionsPopupController.show();
            isHistoryListOpen = true;
        }
    }

    @FXML
    public void addNewComputer() {
        AddEditComputerPopup addEditComputerPopup = new AddEditComputerPopup(mainPane, false);
        addEditComputerPopup.openPopup(null);
    }

    private void updateCounters() {
        totalComputers.textProperty().bind(ComputerData.getInstance().computerListSizeProperty().asString());
        stationCounter.textProperty().bind(ComputerData.getInstance().getStationsCounterItems().asString());
        rcgwCounter.textProperty().bind(ComputerData.getInstance().getRcgwCounterItems().asString());
    }

    private void connectToVNC(String ip) {
        if (!Utils.isValidateIpAddress(ip)) {
            logger.info("Wrong ip address: " + ip + " Can't to connect to client");
            AlertPopupController alertPopupController = new AlertPopupController();
            alertPopupController.showAlert(KorTypes.AlertTypes.WARNING, Utils.WRONG_IP_ADDRESS_MASSAGE, mainPane);
            quickConnectTextField.requestFocus();
            return;
        }

        LastConnectionData.getInstance().addHistoryItemIfNotExist((new LastConnectionItem(ip)));
        lastConnectionsPopupController.hide();
        isHistoryListOpen = false;
        lastConnectionsPopupController.getLastConnectionListController().getLastConnectionList().getSelectionModel().select(0);
        VNCRemote.connect(ip, isViewOnly);
    }

    @FXML
    public void DownComputerInList() {
        if (isAllowedToMoveRows) {
            int currentIndex = computerListController.getCurrent();
            if (currentIndex >= 0 && currentIndex < ComputerData.getInstance().getComputersList().size() - 1) {
//                Collections.swap(ComputerData.getInstance().getComputersList(), currentIndex, currentIndex + 1);
                ComputerData.getInstance().swapRows(currentIndex, currentIndex + 1);
                computerListController.getComputerListView().getSelectionModel().select(currentIndex + 1);
                computerListController.getComputerListView().scrollTo(currentIndex + 1);
            }
        } else {
            AlertPopupController alertPopupController = new AlertPopupController();
            alertPopupController.showAlert(KorTypes.AlertTypes.INFO, Utils.CAN_NOT_MOVE_ROW_INFO, this);
        }
    }

    @FXML
    public void UpComputerInList() {
        if (isAllowedToMoveRows) {
            int currentIndex = computerListController.getCurrent();
            if (currentIndex > 0 && currentIndex <= ComputerData.getInstance().getComputersList().size() - 1) {
                ComputerData.getInstance().swapRows(currentIndex, currentIndex - 1);
//                Collections.swap(ComputerData.getInstance().getComputersList(), currentIndex, currentIndex - 1);
                computerListController.getComputerListView().getSelectionModel().select(currentIndex - 1);
                computerListController.getComputerListView().scrollTo(currentIndex - 1);
            }
        } else {
            AlertPopupController alertPopupController = new AlertPopupController();
            alertPopupController.showAlert(KorTypes.AlertTypes.INFO, Utils.CAN_NOT_MOVE_ROW_INFO, this);
        }
    }


    @Override
    public void showPane() {
        this.setVisible(true);
        this.setStyle("-fx-background-color : #02050A");
        this.toFront();
        logger.trace("showPane");
    }

    public LastConnectionsPopupController getLastConnectionsPopupController() {
        return lastConnectionsPopupController;
    }

    public void setHistoryListOpen(boolean historyListOpen) {
        isHistoryListOpen = historyListOpen;
    }

    public JTextFieldController getSearchAreaController() {
        return searchAreaController;
    }
}
