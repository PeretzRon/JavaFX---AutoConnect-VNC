package com.kerernor.autoconnect.view;

import com.kerernor.autoconnect.model.Computer;
import com.kerernor.autoconnect.model.ComputerData;
import com.kerernor.autoconnect.model.LastConnectionData;
import com.kerernor.autoconnect.model.LastConnectionItem;
import com.kerernor.autoconnect.script.VNCRemote;
import com.kerernor.autoconnect.util.KorEvents;
import com.kerernor.autoconnect.util.ThreadManger;
import com.kerernor.autoconnect.util.Utils;
import com.kerernor.autoconnect.view.popups.AddEditComputerPopup;
import com.kerernor.autoconnect.view.popups.AlertPopupController;
import com.kerernor.autoconnect.view.screens.AboutScreenController;
import com.kerernor.autoconnect.view.screens.PingerScreenController;
import com.kerernor.autoconnect.view.screens.RemoteScreenController;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.apache.log4j.Logger;

import java.util.Collections;

public class MainController extends AnchorPane {

    @FXML
    private RemoteScreenController remoteScreenController;
    @FXML
    private PingerScreenController pingerScreenController;
    @FXML
    private AboutScreenController aboutScreenController;

    @FXML
    private LastConnectionsPopupController lastConnectionsPopupController;

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
    private Button btnAbout;


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
    private ImageView openCloseHistoryImage;

    private static MainController instance = new MainController();
    private final Logger logger = Logger.getLogger(MainController.class);
//    private boolean isViewOnly = false;
    private Button currentSelectedMenuButton;
//    private final BooleanProperty isHistoryListEmpty = new SimpleBooleanProperty(true);
//    private boolean isHistoryListOpen = false;

    public static MainController getInstance() {
        return instance;
    }

    public void initialize() {
        logger.trace("MainController.initialize");
        currentSelectedMenuButton = btnRemoteScreen;
        pingerScreenController.setVisible(false);
        aboutScreenController.setVisible(false);
        btnRemoteScreen.getStyleClass().add("selected-menu-item");
//        pnlOverview.toFront();
        remoteScreenController.showPane();
//        FilteredList<Computer> computerFilteredList = new FilteredList<>(ComputerData.getInstance().getComputersList(), computer -> true);
//        FilteredList<LastConnectionItem> historySearchFilteredList = new FilteredList<>(LastConnectionData.getInstance().getLastConnectionItems(), pingItemsScrollPane -> true);
//        computerListController.setPaneBehind(this.pnlOverview);
//        computerListController.loadList(computerFilteredList);
//        lastConnectionsPopupController.setList(historySearchFilteredList);
//        isHistoryListEmpty.set(historySearchFilteredList.size() == 0);
//        openCloseHistoryImage.disableProperty().bind(isHistoryListEmpty);
//        updateCounters();
//
//        Platform.runLater(() -> quickConnectTextField.requestFocus());



//        LastConnectionData.getInstance().getLastConnectionItems().addListener((ListChangeListener<? super LastConnectionItem>) c -> {
//            if (c.getList().size() == 0) {
//                lastConnectionsPopupController.hide();
//                isHistoryListOpen = false;
//                isHistoryListEmpty.set(true);
//            } else {
//                isHistoryListEmpty.set(false);
//            }
//        });

//        quickConnectBtn.setOnAction(actionEvent -> {
//            connectToVNC(quickConnectTextField.getText());
//        });
//
//        searchAreaController.getSearch().setOnKeyReleased(keyEvent -> {
//            String input = searchAreaController.getSearch().getText();
//
//            computerFilteredList.setPredicate(input.isEmpty() ? computer -> true :
//                    computer -> computer.getIp().contains(input) ||
//                            computer.getName().contains(input) ||
//                            computer.getItemLocation().contains(input));
//            computerListController.scrollTo(0);
//        });

//        // TODO: delete
//        computerListController.addEventHandler(KorEvents.SearchComputerEvent.SEARCH_COMPUTER_EVENT, event -> {
//            event.consume();
//        });
//
//        computerListController.addEventHandler(KorEvents.ConnectVNCEvent.CONNECT_VNC_EVENT_EVENT, event -> {
//            connectToVNC(event.getIpAddress());
//        });
//
//        viewOnlyCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
//            isViewOnly = newValue;
//        });
//
//        openCloseHistoryImage.setOnMouseClicked(event -> {
//                if (lastConnectionsPopupController.isShow()) {
//                    lastConnectionsPopupController.hide();
//                    isHistoryListOpen = false;
//                } else {
//                    openLastConnectionPopupController();
//                }
//        });
//
//        lastConnectionsPopupController.addEventFilter(KorEvents.SearchHistoryConnectionEvent.SEARCH_HISTORY_CONNECTION_EVENT_EVENT_TYPE, event -> {
//            event.consume();
//            quickConnectTextField.setText(event.getLastConnectionItem().getIp());
//            lastConnectionsPopupController.hide();
//            isHistoryListOpen = false;
//            quickConnectTextField.requestFocus();
//            quickConnectTextField.selectEnd();
//        });

    }

//    private void openLastConnectionPopupController() {
//        if (LastConnectionData.getInstance().getLastConnectionItems().size() > 0) {
//            logger.trace("openLastConnectionPopupController");
//            lastConnectionsPopupController.show();
//            isHistoryListOpen = true;
//        }
//    }

    public void handleClicks(ActionEvent actionEvent) {
        logger.trace("ChangeScreenHandler");
        if (actionEvent.getSource() == btnRemoteScreen && currentSelectedMenuButton != btnRemoteScreen) {
            currentSelectedMenuButton = btnRemoteScreen;
//            pnlOverview.setStyle("-fx-background-color : #02030A");
            btnRemoteScreen.getStyleClass().add("selected-menu-item");
            btnPingerScreen.getStyleClass().remove("selected-menu-item");
            btnAbout.getStyleClass().remove("selected-menu-item");
//            pnlOverview.toFront();
            remoteScreenController.showPane();
        }
        if (actionEvent.getSource() == btnPingerScreen && currentSelectedMenuButton != btnPingerScreen) {
            currentSelectedMenuButton = btnPingerScreen;
            btnPingerScreen.getStyleClass().add("selected-menu-item");
            btnRemoteScreen.getStyleClass().remove("selected-menu-item");
            btnAbout.getStyleClass().remove("selected-menu-item");
            pingerScreenController.showPane();
        }

        if (actionEvent.getSource() == btnAbout && currentSelectedMenuButton != btnAbout) {
            currentSelectedMenuButton = btnAbout;
            btnAbout.getStyleClass().add("selected-menu-item");
            btnRemoteScreen.getStyleClass().remove("selected-menu-item");
            btnPingerScreen.getStyleClass().remove("selected-menu-item");
            aboutScreenController.showPane();
        }

        if (actionEvent.getSource() == btnExitApp) {
            ThreadManger.getInstance().shutDown();
            Platform.exit();
        }
    }

//    private void updateCounters() {
//        totalComputers.textProperty().bind(ComputerData.getInstance().computerListSizeProperty().asString());
//        stationCounter.textProperty().bind(ComputerData.getInstance().getStationsCounterItems().asString());
//        rcgwCounter.textProperty().bind(ComputerData.getInstance().getRcgwCounterItems().asString());
//    }

//    public void addNewComputer() {
//        AddEditComputerPopup addEditComputerPopup = new AddEditComputerPopup(pnlOverview, false);
//        addEditComputerPopup.openPopup(null);
//    }

//    private void connectToVNC(String ip) {
//        if (!Utils.isValidateIpAddress(ip)) {
//            logger.info("Wrong ip address: " + ip + " Can't to connect to client");
//
//            // TODO: make singleton
//            AlertPopupController alertPopupController = new AlertPopupController(mainPane);
//            alertPopupController.show();
//            quickConnectTextField.requestFocus();
//            return;
//        }
//
//        LastConnectionData.getInstance().addHistoryItemIfNotExist((new LastConnectionItem(ip)));
//        lastConnectionsPopupController.hide();
//        isHistoryListOpen = false;
//        lastConnectionsPopupController.getLastConnectionListController().getLastConnectionList().getSelectionModel().select(0);
//        VNCRemote.connect(ip, isViewOnly);
//    }

//    public void DownComputerInList() {
//        int currentIndex = computerListController.getCurrent();
//        if (currentIndex >= 0 && currentIndex < ComputerData.getInstance().getComputersList().size() - 1) {
//            Collections.swap(ComputerData.getInstance().getComputersList(), currentIndex, currentIndex + 1);
//            computerListController.getComputerListView().getSelectionModel().select(currentIndex + 1);
//            computerListController.getComputerListView().scrollTo(currentIndex + 1);
//        }
//
//    }
//
//    public void UpComputerInList() {
//        int currentIndex = computerListController.getCurrent();
//        if (currentIndex > 0 && currentIndex <= ComputerData.getInstance().getComputersList().size() - 1) {
//            Collections.swap(ComputerData.getInstance().getComputersList(), currentIndex, currentIndex - 1);
//            computerListController.getComputerListView().getSelectionModel().select(currentIndex - 1);
//            computerListController.getComputerListView().scrollTo(currentIndex - 1);
//        }
//    }

//    public LastConnectionsPopupController getLastConnectionsPopupController() {
//        return lastConnectionsPopupController;
//    }
//
//    public void setHistoryListOpen(boolean historyListOpen) {
//        isHistoryListOpen = historyListOpen;
//    }
}