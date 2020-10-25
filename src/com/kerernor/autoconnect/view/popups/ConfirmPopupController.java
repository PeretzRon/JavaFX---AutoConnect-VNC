package com.kerernor.autoconnect.view.popups;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.Computer;
import com.kerernor.autoconnect.model.ComputerData;
import com.kerernor.autoconnect.model.Pinger;
import com.kerernor.autoconnect.model.PingerData;
import com.kerernor.autoconnect.util.KorTypes;
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
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.Future;

public class ConfirmPopupController extends GridPane {

    private Logger logger = Logger.getLogger(ConfirmPopupController.class);

    @FXML
    private Label mainTitle;

    @FXML
    private Label subTitle;

    private final Parent paneBehind;
    private Computer computer = null;
    private Pinger pingerItem = null;
    private final Object object;
    private Stage stage;

    private KorTypes.ConfirmPopUpControllerTypes callback;

    public ConfirmPopupController(Parent paneBehind, Object object) {
        super();
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
        stage.show();
        Utils.centerNewStageToBehindStage(paneBehind, stage);

        // Blur the pane behind
        paneBehind.effectProperty().setValue(Utils.getBlurEffect());
        logger.info("paneBehind - BlueEffect");

        return callback;

    }

    public void closeClickAction() {
        // Revert the blur effect from the pane behind
        logger.trace("close confirm popup - NO Action");
        paneBehind.effectProperty().setValue(Utils.getEmptyEffect());
        callback = KorTypes.ConfirmPopUpControllerTypes.EXIT;
        logger.info("paneBehind - no effect");
        stage.close();
    }

    public void confirmClickAction() {
        logger.trace("close confirm popup - CONFIRM THE Action");
        callback = KorTypes.ConfirmPopUpControllerTypes.CONFIRM;
        if (computer != null) {
            ComputerData.getInstance().remove(computer);
        } else if (pingerItem != null) {
            PingerData.getInstance().remove(pingerItem);
        }

        closeClickAction();
    }

    public KorTypes.ConfirmPopUpControllerTypes getCallback() {
        return callback;
    }
}
