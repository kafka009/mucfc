package com.mucfc.thread;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ManagedBlocker;
import java.util.concurrent.atomic.AtomicBoolean;

public class ManagedBlockTest {
    /**
     * ForkJoinPool.ManagedBlocker接口有两个方法，其中block方法可能会阻塞线程，若它返回true，则表示不需要阻塞线程了；
     * isReleasable检查线程是否需要阻塞，如果它返回true，表示不需要阻塞。
     */
    public static void main(String[] args) throws InterruptedException {
        AtomicBoolean flag = new AtomicBoolean(false);
        Object obj = new Object();
        ForkJoinPool pool = ForkJoinPool.commonPool();
        ManagedBlocker block = new ManagedBlocker() {
            @Override
            public boolean isReleasable() {
                return flag.get();
            }

            @Override
            public boolean block() throws InterruptedException {
                synchronized (obj) {
                    obj.wait();
                }
                return false;
            }
        };

        pool.submit(() -> {
            synchronized (obj) {
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                flag.set(true);
                obj.notifyAll();
            }
        });

        ForkJoinPool.managedBlock(block);
        System.out.println(111);
    }
}
