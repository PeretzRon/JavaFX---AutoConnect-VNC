package com.kerernor.autoconnect.view.popups;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.Computer;
import com.kerernor.autoconnect.model.ComputerData;
import com.kerernor.autoconnect.util.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ConfirmPopupController extends GridPane {

    @FXML
    private Label mainTitle;

    @FXML
    private Label subTitle;

    private final Parent paneBehind;
    private final Computer computer;
    private Stage stage;

    public ConfirmPopupController(Parent paneBehind, Computer computer) {
        super();
        this.paneBehind = paneBehind;
        this.computer = computer;
    }

    @FXML
    private void initialize() {
        mainTitle.setText(Utils.TEXT_CONFIRM_DELETE_TITLE);
        subTitle.setText(Utils.TExT_CONFIRM_DELETE_COMPUTER_MESSAGE + computer.toString());
    }

    public GridPane loadView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Utils.CONFIRM_POPUP));

        loader.setController(this);

        try {
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public void openPopup() {
        Scene scene = new Scene(this.loadView());

        stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);

        // Set out of focus closing ability
        stage.focusedProperty().addListener((observableValue, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                closeClickAction();
            }
        });

        // Prevent the window from closing in case of out of focus
        stage.initModality(Modality.NONE);
        stage.initOwner(paneBehind.getScene().getWindow());
        stage.show();

        // Blur the pane behind
        paneBehind.effectProperty().setValue(Utils.getBlurEffect());

    }

    public void closeClickAction() {
        // Revert the blur effect from the pane behind
        paneBehind.effectProperty().setValue(Utils.getEmptyEffect());
        stage.close();
    }

    public void confirmClickAction() {
        ComputerData.getInstance().remove(computer);
        closeClickAction();
    }
}
