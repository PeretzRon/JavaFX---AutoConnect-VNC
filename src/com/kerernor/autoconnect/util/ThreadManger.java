package com.kerernor.autoconnect.util;

import org.apache.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadManger {
    private static Logger logger = Logger.getLogger(ThreadManger.class);
    private static ThreadManger instance = null;
    private ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(15);
    private final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10);

    private ThreadManger() {

    }

    public static ThreadManger getInstance() {
        if (instance == null) {
            logger.trace("Create ThreadManger ");
            instance = new ThreadManger();
        }
        return instance;
    }


    public ScheduledExecutorService getScheduledThreadPool() {
        return scheduledThreadPool;
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public void shutDown() {
        logger.info("shutDown ThreadPoolExecutor ");
        threadPoolExecutor.shutdown();
        scheduledThreadPool.shutdown();
    }
}
