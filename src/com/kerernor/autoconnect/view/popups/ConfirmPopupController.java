package com.kerernor.autoconnect.view.popups;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.Computer;
import com.kerernor.autoconnect.model.Pinger;
import com.kerernor.autoconnect.util.KorTypes;
import com.kerernor.autoconnect.util.ThreadManger;
import com.kerernor.autoconnect.util.Utils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ConfirmPopupController extends GridPane {

    private Logger logger = Logger.getLogger(ConfirmPopupController.class);

    @FXML
    private Label mainTitle;

    @FXML
    private Label subTitle;

    private Parent paneBehind;
    private Computer computer = null;
    private Pinger pingerItem = null;
    private Object object;
    private Stage stage;
    ScheduledFuture<?> timer;

    private static ConfirmPopupController instance = null;
    private KorTypes.ConfirmPopUpControllerTypes callback;

    private ConfirmPopupController() {

    }

    public static ConfirmPopupController getInstance() {
        if (instance == null) {
            instance = new ConfirmPopupController();
        }

        return instance;
    }

    public void setConfiguration(Parent paneBehind, Object object) {
        this.paneBehind = paneBehind;
        this.object = object;
        castObjectToItem();
    }

    private void castObjectToItem() {
        if (object instanceof Computer) {
            computer = (Computer) object;
        } else if (object instanceof Pinger) {
            pingerItem = (Pinger) object;
        }
    }

    @FXML
    private void initialize() {
        mainTitle.setText(Utils.TEXT_CONFIRM_DELETE_TITLE);
        if (computer != null) {
            subTitle.setText(Utils.TExT_CONFIRM_DELETE_COMPUTER_MESSAGE + computer.toString());
        } else if (pingerItem != null) {
            subTitle.setText(Utils.TExT_CONFIRM_DELETE_COMPUTER_MESSAGE + pingerItem.toString());
        }

    }

    public GridPane loadView() {
        logger.trace("loadView");
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Utils.CONFIRM_POPUP));

        loader.setController(this);

        try {
            return loader.load();
        } catch (IOException e) {
            logger.error("Error load view", e);
            e.printStackTrace();
        }

        return null;
    }


    public KorTypes.ConfirmPopUpControllerTypes openPopup() {
        logger.trace("openPopup");
        Scene scene = new Scene(this.loadView());

        stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);

        // Prevent the window from closing in case of out of focus
        stage.initModality(Modality.NONE);
        stage.initOwner(paneBehind.getScene().getWindow());

        // Blur the pane behind
        paneBehind.effectProperty().setValue(Utils.getBlurEffect());
        logger.info("paneBehind - BlueEffect");

        timer = ThreadManger.getInstance().getScheduledThreadPool().schedule(() -> {
            Platform.runLater(this::closeClickAction);
        }, Utils.TIME_FOR_CLOSE_POPUP, TimeUnit.MILLISECONDS);


        Utils.showStageOnTopAndWait(stage);
        Utils.centerNewStageToBehindStage(paneBehind, stage);


        return callback;

    }

    @FXML
    public void closeClickAction() {
        logger.trace("close confirm popup - NO Action");
        callback = KorTypes.ConfirmPopUpControllerTypes.EXIT;
        stopTimer();
        paneBehind.effectProperty().setValue(Utils.getEmptyEffect());
        closeStage();
    }

    @FXML
    public void confirmClickAction() {
        logger.trace("close confirm popup - CONFIRM THE Action");
        callback = KorTypes.ConfirmPopUpControllerTypes.CONFIRM;
        closeStage();
    }

    private void closeStage() {
        logger.trace("closeStage");
        paneBehind.effectProperty().setValue(Utils.getEmptyEffect());
        logger.info("paneBehind - no effect");
        stage.close();

    }

    public KorTypes.ConfirmPopUpControllerTypes getCallback() {
        return callback;
    }

    private void stopTimer() {
        logger.info("ConfirmPopupController.stopTimer");
        if (timer != null) {
            timer.cancel(true);
            timer = null;
        }
    }

}
