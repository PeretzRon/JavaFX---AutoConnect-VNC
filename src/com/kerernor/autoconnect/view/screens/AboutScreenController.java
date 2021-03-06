package com.kerernor.autoconnect.view.screens;

import animatefx.animation.FadeIn;
import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.util.Utils;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;

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
    @FXML
    private WebView webView;

    private Logger logger = LogManager.getLogger(AboutScreenController.class);
    FXMLLoader loader = null;
    private static AboutScreenController instance = null;
    URL url = this.getClass().getResource("/com/kerernor/autoconnect/images/app-icon-animated.html");


    @FXML
    public void initialize() {
        webView.getEngine().load(url.toString());
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
        logger.debug("showPane");
        webView.getEngine().load(url.toString());
        this.setVisible(true);
        this.setStyle(Utils.SCREEN_BACKGROUND_COLOR);
        this.toFront();
        new FadeIn(this).setSpeed(1.5).play();
    }

    public Label getAboutSecondLine() {
        return aboutSecondLine;
    }

    public Label getAboutFirstLine() {
        return aboutFirstLine;
    }
}
