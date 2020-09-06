package com.kerernor.autoconnect.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.kerernor.autoconnect.models.Computer;
import com.kerernor.autoconnect.models.ComputerData;
import com.kerernor.autoconnect.models.ComputerNode;
import com.kerernor.autoconnect.scripts.VNCRemote;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private AnchorPane mainPane;

    @FXML
    private JFXListView<ComputerNode> listView;

    @FXML
    private TextField search;

    @FXML
    private Button btnRemoteScreen;

    @FXML
    private Button btnExitApp;

    @FXML
    private Button btnSettingsScreen;

    @FXML
    private Pane pnlCustomer;

    @FXML
    private Pane pnlOrders;

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

    public JFXListView<ComputerNode> getListView() {
        return listView;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FilteredList<ComputerNode> computerFilteredList = new FilteredList<>(ComputerData.getInstance().getComputersList(), computerNode -> true);
        listView.setItems(ComputerData.getInstance().getComputersList());
        listView.setItems(computerFilteredList);
        search.setOnKeyReleased(keyEvent -> {
            String input = search.getText();
            computerFilteredList.setPredicate(input.isEmpty() ? computer -> true :
                    computerNode -> computerNode.getItem().getIp().contains(input) ||
                            computerNode.getItem().getName().contains(input) ||
                            computerNode.getItem().getItemLocation().contains(input));
        });


        listView.setCellFactory(nodeItemListView -> {
            ListCell<ComputerNode> cell = new ListCell<>() {
                @Override
                protected void updateItem(ComputerNode item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null) {
                        setGraphic(null);
                        setStyle(" -fx-background-color: transparent;");
                    } else {
                        setGraphic(item.getNode());
                    }
                }
            };

            return cell;
        });

        Platform.runLater(() -> quickConnectTextField.requestFocus());
        quickConnectTextField.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                VNCRemote.connect(quickConnectTextField.getText());
            }
        });
        quickConnectBtn.setOnAction(actionEvent -> {
            VNCRemote.connect(quickConnectTextField.getText());
        });

        updateCounters();
    }

    public void select() throws IOException {
        System.out.println("Remove Item: " + listView.getSelectionModel().getSelectedItem());

    }

    public void handleClicks(ActionEvent actionEvent) {
        if (actionEvent.getSource() == btnRemoteScreen) {
            pnlOverview.setStyle("-fx-background-color : #02030A");
            pnlOverview.toFront();
        }
        if (actionEvent.getSource() == btnSettingsScreen) {
            pnlOrders.setStyle("-fx-background-color : #464F67");
            pnlOrders.toFront();
        }

        if (actionEvent.getSource() == btnExitApp) {
            Platform.exit();
        }
    }

    private void updateCounters() {
        totalComputers.textProperty().bind(Bindings.size((listView.getItems())).asString());
        stationCounter.textProperty().bind(Bindings.concat(ComputerData.getInstance().getStationsCounterItems()));
        rcgwCounter.textProperty().bind(Bindings.concat(ComputerData.getInstance().getRcgwCounterItems()));

    }

    public void addNewComputer() {
        addEditComputerDialog(false, null);
    }

    public void addEditComputerDialog(boolean isEdit, Computer computerToEdit) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPane.getScene().getWindow());
        dialog.initStyle(StageStyle.UNDECORATED);
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("../fxml/addEditItemDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        DialogController dialogController = fxmlLoader.getController();
        if (isEdit) {
            // load the current date to fields
            dialogController.getComputerIPAddress().setText(computerToEdit.getIp());
        }
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // user press OK
            dialogController.processResults();
            listView.getSelectionModel().select(listView.getItems().size() - 1);
            listView.scrollTo(listView.getItems().size() - 1);
        }
    }


}