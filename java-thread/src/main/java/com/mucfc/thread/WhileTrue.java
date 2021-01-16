package com.mucfc.thread;

import java.util.LinkedHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * future.cancel不能中断while true
 */
public class WhileTrue {
    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, new LinkedBlockingDeque<>(1));
        for (int i = 0; i < 1; i++) {
            Future future = threadPoolExecutor.submit(() -> {
                while (!Thread.interrupted()) {//
                    System.out.println(System.currentTimeMillis());
                }
            });
        }
        LinkedHashMap hashMap = new LinkedHashMap();
        hashMap.entrySet();
//        threadPoolExecutor.shutdown();// 不会取消任务，线程池里的线程状态不会变
//        threadPoolExecutor.shutdownNow();// 依赖于!Thread.interrupted()
        threadPoolExecutor = null;
    }
}
