package com.kerernor.autoconnect.view.screens;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.util.Utils;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import org.apache.log4j.Logger;

import java.io.IOException;

public class AboutScreenController extends Pane implements IDisplayable {

    @FXML
    private Pane pnlAbout;
    @FXML
    private Label aboutSecondLine;
    @FXML
    private Label aboutFirstLine;

    private Logger logger = Logger.getLogger(AboutScreenController.class);
    FXMLLoader loader = null;

    @FXML
    public void initialize() {
        aboutFirstLine.setText(Utils.COPYRIGHT);
        aboutSecondLine.textProperty().bind(Bindings.concat(Utils.VERSION, Utils.VERSION_NUMBER));
    }

    public AboutScreenController() {
        loadView();
    }

    private Pane loadView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Utils.ABOUT_SCREEN));
        loader.setController(this);
        loader.setRoot(this);

        try {
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void showPane() {
        this.setVisible(true);
        this.setStyle("-fx-background-color : #02050A");
        this.toFront();
        logger.trace("showPane");
    }
}
