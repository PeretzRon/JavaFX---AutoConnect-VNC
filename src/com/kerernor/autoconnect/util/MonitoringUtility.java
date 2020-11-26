package com.kerernor.autoconnect.util;

import com.kerernor.autoconnect.view.components.JSearchableTextFlowController;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import org.apache.log4j.Logger;

public class MonitoringUtility {

    Logger logger = Logger.getLogger(MonitoringUtility.class);
    private static MonitoringUtility instance = null;
    private Boolean isContinue = true;

    public static MonitoringUtility getInstance() {
        if (instance == null) {
            instance = new MonitoringUtility();
        }
        return instance;
    }

    private MonitoringUtility() {
    }

    public void start() {
        logger.trace("start");
        ThreadManger.getInstance().getThreadPoolExecutor().execute(() -> {
            Thread.currentThread().setName(Utils.MONITORING_UTILITY_THREAD_NAME);
            while (isContinue) {
                try {
                    synchronized (isContinue) {
                        isContinue.wait(Utils.TIME_FOR_PERIOD_OF_MONITORING_UTILITY);
                    }
                    logger.debug("MonitoringUtility execute");
                    VirtualFlow virtualFlow = (VirtualFlow) KorCommon.getInstance().getRemoteScreenController().getComputerListController().getComputerListView().getChildrenUnmodifiable().get(0);
                    clearMapOfTextFlow();
                } catch (InterruptedException ignore) {
                    synchronized (isContinue) {
                        isContinue = false;
                        logger.info("Stop MonitoringUtility");
                    }
                }
            }
        });
    }

    private void clearMapOfTextFlow() {
        int sizeOfElementsBeforeClearing = JSearchableTextFlowController.getActiveSearchableTextFlowMap().size();
        JSearchableTextFlowController.getActiveSearchableTextFlowMap().clear();
        logger.info("clearMapOfTextFlow - was " + sizeOfElementsBeforeClearing + " elements, now: 0 elements");
    }
}
