package com.kerernor.autoconnect.util;

import com.kerernor.autoconnect.Main;
import javafx.scene.Parent;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Utils {

    private static Logger logger = Logger.getLogger(Utils.class);
    public static Map<String, Image> appImages = new HashMap<>();

    // Base paths
    public static final String BASE_PATH = "com/kerernor/autoconnect/";
    public static final String VIEWS_BASE_PATH = "view/";
    public static final String IMAGES_BASE_PATH = BASE_PATH + "images/";
    public static final String VIEWS_POPUPS_BASE_PATH = VIEWS_BASE_PATH + "popups/";
    public static final String CONFIG_BASE_PATH = BASE_PATH + "config/";

    // View paths
    public static final String MAIN_VIEW = VIEWS_BASE_PATH + "main.fxml";
    public static final String COMPUTER_LIST = VIEWS_BASE_PATH + "computerList.fxml";
    public static final String COMPUTER_ROW_VIEW = VIEWS_BASE_PATH + "computerRow.fxml";
    public static final String SEARCH_AREA = VIEWS_BASE_PATH + "searchArea.fxml";
    public static final String PINGER_LIST = VIEWS_BASE_PATH + "pingListGroup.fxml";
    public static final String PINGER_ROW_VIEW = VIEWS_BASE_PATH + "pingRowItem.fxml";
    public static final String CONFIRM_POPUP = VIEWS_POPUPS_BASE_PATH + "confirmPopup.fxml";
    public static final String ALERT_POPUP = VIEWS_POPUPS_BASE_PATH + "alertPopup.fxml";
    public static final String ADD_EDIT_COMPUTER_POPUP = VIEWS_POPUPS_BASE_PATH + "addEditComputerPopup.fxml";
    public static final String ADD_EDIT_PINGER_ITEMS = VIEWS_POPUPS_BASE_PATH + "addEditPingerItems.fxml";
    public static final String PING_GROUP_ITEM = VIEWS_BASE_PATH + "pingGroupItem.fxml";


    // Images file paths
    public static final String RCGW_ICON = IMAGES_BASE_PATH + "antenna.png";
    public static final String STATION_ICON = IMAGES_BASE_PATH + "stationKO.png";
    public static final String APP_ICON = IMAGES_BASE_PATH + "ko.png";

    // Data paths
    public static final String COMPUTER_DATA = "data/data.json";
    public static final String PINGER_DATA = "data/pingData.json";

    // Config files paths
    public static final String LOG_4_J_CONFIG = "config/log4j.properties";
    public static final String APP_SETTINGS = "config/settings.properties";

    // VNC Script paths
    public static String VNC_PROGRAM_PATH = "C:\\Program Files (x86)\\uvnc bvba\\UltraVNC";
    public static String VNC_SCRIPT_PATH = "C:\\Programs\\ControlKO\\vnc\\";

    // App setting properties
    public static final String ULTRA_VNC_PROGRAM_PATH = "UltraVncProgramPath";
    public static final String ULTRA_VNC_SCRIPT_FOR_CONNECTION = "UltraVncScriptForConnection";
    public static final String IS_POPUP_CLOSE_IF_LOSE_FOCUS = "isPopupCloseIfLoseFocus";
    public static boolean IS_POPUP_CLOSE_IF_LOSE_FOCUS_SETTING = true;

    // Sizes and amounts
    public static final int BLUR_SIZE = 5;
    public static final int BLUR_ITERATIONS = 3;

    // Text display
    public static final String TEXT_CONFIRM_DELETE_TITLE = "Deletion confirmation";
    public static final String TExT_CONFIRM_DELETE_COMPUTER_MESSAGE = "Are you sure to delete: ";
    public static final String TEXT_ADD_NEW_COMPUTER_POPUP = "Add new computer";
    public static final String TEXT_EDIT_COMPUTER_POPUP = "Edit ";
    public static final String WRONG_IP_ADDRESS_MASSAGE = "Wrong ip address, try again";
    public static final String TEXT_ADD_NEW_GROUP_PINGER_POPUP_TITTLE = "ADD new group Pinger";
    public static final String APP_NAME = "ControlKO";
    public static final String COPYRIGHT = "Copyright " + "\u00a9" + " Ron Peretz (2020)";
    public static final String VERSION = "Version 1.0.0";


    // Style
    public static final String PINGER_GROUP_ITEM_SELECTED = "pinger-item-selected";

    /**
     * This method gets the blur effect.
     *
     * @return BoxBlur effect.
     */
    public static Effect getBlurEffect() {
        logger.trace("Effect.getBlurEffect");
        BoxBlur blurEffect = new BoxBlur();
        blurEffect.setWidth(BLUR_SIZE);
        blurEffect.setHeight(BLUR_SIZE);
        blurEffect.setIterations(BLUR_ITERATIONS);

        return blurEffect;
    }

    /**
     * This method gets empty effect.
     *
     * @return empty effect (null).
     */
    public static Effect getEmptyEffect() {
        logger.trace("Effect.getEmptyEffect");
        return null;
    }


    public static void centerNewStageToBehindStage(Parent paneBehind, Stage newStage) {
        // Calculate the center position of the parent Stage
        logger.info("centerNewStageToBehindStage");
        Stage primaryStage = (Stage) paneBehind.getScene().getWindow();
        double centerXPosition = primaryStage.getX() + primaryStage.getWidth() / 2d;
        double centerYPosition = primaryStage.getY() + primaryStage.getHeight() / 2d;

        newStage.setX(centerXPosition - newStage.getWidth() / 2d);
        newStage.setY(centerYPosition - newStage.getHeight() / 2d);
    }

    public static boolean isValidateIpAddress(final String ip) {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        return ip.matches(PATTERN);
    }

    public static boolean isNullOrEmptyString(final String str) {
        return str != null && str.length() == 0;
    }

    public static void loadAndSetLoggerSetting() {
        Properties props = new Properties();
        try (InputStream inputStream = new FileInputStream(LOG_4_J_CONFIG)) {
            props.load(inputStream);
            PropertyConfigurator.configure(props);
            logger.info("loadAndSetLoggerSetting");
        } catch (IOException e) {
            logger.error("failed to load log4j settings");
        }
    }

    public static void loadAppSettings() {
        ThreadManger.getInstance().getThreadPoolExecutor().execute(() -> {
            logger.info("loadAppSettings");
            Properties properties = new Properties();

            try (InputStream inputStream = new FileInputStream(APP_SETTINGS)) {
                properties.load(inputStream);
                Utils.VNC_PROGRAM_PATH = properties.getProperty(ULTRA_VNC_PROGRAM_PATH);
                Utils.VNC_SCRIPT_PATH = properties.getProperty(ULTRA_VNC_SCRIPT_FOR_CONNECTION);
                Utils.IS_POPUP_CLOSE_IF_LOSE_FOCUS_SETTING = Boolean.parseBoolean(properties.getProperty(IS_POPUP_CLOSE_IF_LOSE_FOCUS));
            } catch (IOException e) {
                logger.error("failed to load app settings");
            }
        });
    }

    public static Image getImageByName(String name) {
        return appImages.computeIfAbsent(name, s -> new Image(name));
    }
}
