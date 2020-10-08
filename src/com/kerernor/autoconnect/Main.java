package com.kerernor.autoconnect;

import com.kerernor.autoconnect.model.ComputerData;
import com.kerernor.autoconnect.model.PingerData;
import com.kerernor.autoconnect.util.Utils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.scenicview.ScenicView;

import java.io.IOException;

public class Main extends Application {
    private double x, y;
    private Stage primaryStage;
    private AnchorPane rootLayout;

    private Logger logger = Logger.getLogger(Main.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        PropertyConfigurator.configure("D:\\Projects\\IntelliJ Projects\\AutoConnectKO\\src\\com\\kerernor\\autoconnect\\config\\log4j.properties");
        logger.trace("********************** Start Main *************************");
        this.primaryStage = primaryStage;
        initRootLayout();
        makeStageDraggable();
        Scene scene = primaryStage.getScene();
//        ScenicView.show(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void initRootLayout() {
        try {
            // Load root layout from fxml file.
            logger.trace("initRootLayout");
            this.rootLayout = FXMLLoader.load(getClass().getResource(Utils.MAIN_VIEW));
            Scene scene = new Scene(rootLayout);

            // Show scene and configure the root layout
            this.primaryStage.setTitle("Remote App");
            this.primaryStage.initStyle(StageStyle.UNDECORATED);   // set stage borderless
            this.primaryStage.setScene(scene);
            this.primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Error to init main stage");
        }
    }

    private void makeStageDraggable() {
        logger.info("makeStageDraggable");
        this.rootLayout.setOnMousePressed(event -> {
            x = event.getSceneX();
            y = event.getSceneY();
        });

        this.rootLayout.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - x);
            primaryStage.setY(event.getScreenY() - y);

        });
    }

    @Override
    public void init() throws Exception {
        ComputerData.getInstance().loadData();
        PingerData.getInstance().loadData();
    }

    @Override
    public void stop() throws Exception {
        ComputerData.getInstance().storeData();
    }
}