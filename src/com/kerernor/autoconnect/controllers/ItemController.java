package com.kerernor.autoconnect.controllers;

import com.kerernor.autoconnect.models.ComputerData;
import com.kerernor.autoconnect.models.ComputerNode;
import com.kerernor.autoconnect.scripts.VNCRemote;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class ItemController extends ListCell<ComputerNode> {

    @FXML
    private Label locationA;

    @FXML
    private Label ip;

    @FXML
    private Label name;

    @FXML
    private ImageView computerType;

    @FXML
    private ImageView removeBtnID;

    public ImageView getRemoveBtnID() {
        return removeBtnID;
    }

    public ImageView getComputerType() {
        return computerType;
    }

    public Label getLocation() {
        return locationA;
    }

    public Label getIp() {
        return ip;
    }

    public Label getName() {
        return name;
    }

    public void removeItem(MouseEvent actionEvent) throws IOException {
        Node node = (Node) actionEvent.getSource();
        ComputerData.getInstance().remove(node.getParent().getParent().getId());
    }

    public void connectToComputer(MouseEvent actionEvent) {
        Node node = (Node) actionEvent.getSource();
        VNCRemote.connect(node.getParent().getParent().getId());
    }

    public void editItem(MouseEvent mouseEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "Main.fxml"));
        Parent root = loader.load();
        MainController controller = loader.getController();

        Scene newScene = new Scene(root);
        Stage newStage = new Stage();
        newStage.setScene(newScene);
        Node node = (Node) mouseEvent.getSource();
//        controller.addEditComputerDialog();
//        newStage.show();

    }

}
