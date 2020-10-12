package com.kerernor.autoconnect.util;

import org.apache.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadManger {
    private Logger logger = Logger.getLogger(ThreadManger.class);
    private static ThreadManger instance = new ThreadManger();
    private ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(15);
    private ThreadManger () {

    }

    public static ThreadManger getInstance() {
        return instance;
    }


    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public void shutDown(){
        logger.info("shutDown ThreadPoolExecutor ");
        threadPoolExecutor.shutdown();
    }
}
