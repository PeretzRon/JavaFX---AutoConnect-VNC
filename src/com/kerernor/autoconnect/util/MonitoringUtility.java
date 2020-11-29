package com.kerernor.autoconnect.util;

import com.kerernor.autoconnect.view.screens.ISearchTextFlow;
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
                        isContinue.wait(Utils.TIME_FOR_PERIOD_OF_MONITORING_UTILITY_MILLI_SECONDS);
                    }
                    logger.debug("MonitoringUtility execute");
                    clearMapOfTextFlow(KorCommon.getInstance().getRemoteScreenController());
                    clearMapOfTextFlow(KorCommon.getInstance().getPingerScreenController());
                } catch (InterruptedException ignore) {
                    synchronized (isContinue) {
                        isContinue = false;
                        logger.info("Stop MonitoringUtility");
                    }
                }
            }
        });
    }

    private void clearMapOfTextFlow(ISearchTextFlow screenNode) {
        int sizeOfElementsBeforeClearing = screenNode.getActiveSearchableTextFlowMap().size();
        screenNode.getActiveSearchableTextFlowMap().clear();
        logger.info("clearMapOfTextFlow " + screenNode.getClass().getSimpleName() + " - was " + +sizeOfElementsBeforeClearing + " elements, now: 0 elements");
    }
}
