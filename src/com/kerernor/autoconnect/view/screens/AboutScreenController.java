package com.kerernor.autoconnect.view.screens;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.util.Utils;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.apache.log4j.Logger;

import java.io.IOException;

public class AboutScreenController extends Pane implements IDisplayable {

    @FXML
    private Pane pnlAbout;
    @FXML
    private Label aboutSecondLine;
    @FXML
    private Label aboutFirstLine;
    @FXML
    private Label appNameLabel;
    @FXML
    private ImageView appImage;

    private Logger logger = Logger.getLogger(AboutScreenController.class);
    FXMLLoader loader = null;
    private static AboutScreenController instance = null;

    @FXML
    public void initialize() {

        aboutFirstLine.setText(Utils.COPYRIGHT);
        aboutSecondLine.textProperty().bind(Bindings.concat(Utils.VERSION, Utils.VERSION_NUMBER));
        appNameLabel.setText(Utils.APP_NAME);
    }

    public static AboutScreenController getInstance() {
        if (instance == null) {
            instance = new AboutScreenController();
        }
        return instance;
    }

    private AboutScreenController() {
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
        logger.trace("showPane");
        this.setVisible(true);
        this.setStyle("-fx-background-color : #02050A");
        this.toFront();
//        showAnimations();

    }


    private void showAnimations() {
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(4000), appImage);
        appImage.setTranslateX(-1000);
        translateTransition.setToX(0);
        translateTransition.setToY(0);
        translateTransition.setToZ(0);
        translateTransition.setRate(1);
        translateTransition.play();
    }

    public Label getAboutSecondLine() {
        return aboutSecondLine;
    }

    public Label getAboutFirstLine() {
        return aboutFirstLine;
    }
}
