package com.kerernor.autoconnect;

import com.kerernor.autoconnect.model.ComputerData;
import com.kerernor.autoconnect.model.LastConnectionData;
import com.kerernor.autoconnect.model.LastRemoteDriveData;
import com.kerernor.autoconnect.model.PingerData;
import com.kerernor.autoconnect.util.KorCommon;
import com.kerernor.autoconnect.util.ThreadManger;
import com.kerernor.autoconnect.util.Utils;
import com.kerernor.autoconnect.view.MainController;
import com.kerernor.autoconnect.view.screens.RemoteScreenController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class Main extends Application {

    // set log4j2 configuration
    static {
        String filePath = new File("").getAbsolutePath();
        filePath = filePath.concat(File.separator + "config" + File.separator + Utils.LOG_4_J_CONFIG);
        System.setProperty("log4j.configurationFile", filePath);
    }

    private double x, y;
    private Stage primaryStage;
    private AnchorPane rootLayout;
    private FXMLLoader loader;
    private Logger logger = LogManager.getLogger(Main.class);

    @Override
    public void start(Stage primaryStage) throws Exception {

        logger.info("********************** Start Main *************************");
        logger.info("Java version: " + System.getProperty("sun.arch.data.model"));
        this.primaryStage = primaryStage;
        initRootLayout();
        makeStageDraggable();
        KorCommon.getInstance().getMonitoringUtility().start();
        Scene scene = primaryStage.getScene();
//        ScenicView.show(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void initRootLayout() {
        try {
            // Load root layout from fxml file.
            logger.debug("initRootLayout");
            KorCommon.getInstance();
            loader = new FXMLLoader(getClass().getResource(Utils.MAIN_VIEW));
            this.rootLayout = loader.load();
            Scene scene = new Scene(rootLayout);
            KorCommon.getInstance().setMainController(loader.getController());

            // Show scene and configure the root layout
            this.primaryStage.setTitle(Utils.APP_NAME);
            this.primaryStage.initStyle(StageStyle.UNDECORATED);   // set stage borderless
            this.primaryStage.setScene(scene);
            this.primaryStage.getIcons().add(new Image(Utils.APP_ICON));
            this.primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Error to init main stage");
        }
    }

    private void makeStageDraggable() {
        logger.debug("makeStageDraggable");
        this.rootLayout.setOnMousePressed(event -> {
            x = event.getSceneX();
            y = event.getSceneY();
            closePopupIfOpened(event);
        });

        this.rootLayout.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - x);
            primaryStage.setY(event.getScreenY() - y);
        });
    }

    private void closePopupIfOpened(MouseEvent event) {
        if (!(event.getTarget() instanceof ImageView)) {
            MainController mainController = loader.getController();
            final RemoteScreenController remoteScreenController = mainController.getRemoteScreenController();
            if (remoteScreenController.getLastConnectionsPopupController().isShow()) {
                remoteScreenController.getLastConnectionsPopupController().hide();
                remoteScreenController.setHistoryListOpen(false);
                logger.debug("hide last connection popup");
            }
        }
    }

    @Override
    public void init() throws Exception {
        Utils.loadAppSettings();
        ComputerData.getInstance().loadData();
        PingerData.getInstance().loadData();
        LastConnectionData.getInstance().loadData();
        LastRemoteDriveData.getInstance().loadData();
    }

    @Override
    public void stop() throws Exception {
        logger.debug("Main.stop");
        ComputerData.getInstance().storeData();
        PingerData.getInstance().storeData();
        LastConnectionData.getInstance().storeData();
        LastRemoteDriveData.getInstance().storeData();
        ThreadManger.getInstance().shutDown();
        LogManager.shutdown();
        System.exit(0);
    }
}