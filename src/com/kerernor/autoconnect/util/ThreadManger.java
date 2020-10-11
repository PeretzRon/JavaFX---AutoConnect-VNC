package com.kerernor.autoconnect.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadManger {
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
}
