//package com.kerernor.autoconnect.view;
//
//import com.kerernor.autoconnect.Main;
//import com.kerernor.autoconnect.util.KorCommon;
//import com.kerernor.autoconnect.util.Utils;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.control.ProgressBar;
//import javafx.scene.layout.Pane;
//
//import java.io.IOException;
//
//public class GuardController extends Pane {
//
//    @FXML
//    private Pane mainPane;
//    @FXML
//    private ProgressBar progressBar;
//
//    private FXMLLoader loader;
//    private static GuardController instance = null;
//
//    @FXML
//    public void initialize() {
//    }
//
//    public GuardController() {
//        loadView();
//    }
//
//    public static GuardController getInstance() {
//        if (instance == null) {
//            instance =
//        }
//        return instance;
//    }
//
//    private Pane loadView() {
//        loader = new FXMLLoader(Main.class.getResource(Utils.GUARD_SCREEN));
//        loader.setController(this);
//        loader.setRoot(this);
//
//
//        try {
//            return loader.load();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//    public void show() {
//        this.toFront();
//    }
//
//    public void hide() {
//        this.toBack();
//    }
//
//}
