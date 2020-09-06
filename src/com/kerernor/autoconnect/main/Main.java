package com.kerernor.autoconnect.main;

import com.kerernor.autoconnect.models.ComputerData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
    private double x, y;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/Main.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Remote App");
        //set stage borderless
        primaryStage.initStyle(StageStyle.UNDECORATED);

        //drag it here
        root.setOnMousePressed(event -> {
            x = event.getSceneX();
            y = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {

            primaryStage.setX(event.getScreenX() - x);
            primaryStage.setY(event.getScreenY() - y);

        });
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
//        AppSetting.getInstance().restoreSettings();
        ComputerData.getInstance().loadData();
    }

    @Override
    public void stop() throws Exception {
        ComputerData.getInstance().storeData();
    }
}
