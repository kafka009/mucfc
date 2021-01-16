package com.mucfc.thread;

import java.util.concurrent.*;

/**
 * 依赖于afterExecute
 */
public class JavaThreadNumber6 {
    private static final ThreadPoolExecutor executor1 = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS, new SynchronousQueue<>()) {
        protected void afterExecute(Runnable r, Throwable t) {
            if (i == 36) {
                shutdown.run();
                return;
            }
            executor2.submit(runnable);
        }
    };
    private static final ThreadPoolExecutor executor2 = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS, new SynchronousQueue<>()) {
        protected void afterExecute(Runnable r, Throwable t) {
            if (i == 36) {
                shutdown.run();
                return;
            }
            executor3.submit(runnable);
        }
    };
    private static final ThreadPoolExecutor executor3 = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS, new SynchronousQueue<>()) {
        protected void afterExecute(Runnable r, Throwable t) {
            if (i == 36) {
                shutdown.run();
                return;
            }
            executor1.submit(runnable);
        }
    };
    private static final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + "=" + (++i));
            System.out.println(Thread.currentThread().getName() + "=" + (++i));
            System.out.println(Thread.currentThread().getName() + "=" + (++i));
        }
    };
    private static final Runnable shutdown = () -> {
        executor1.shutdown();
        executor2.shutdown();
        executor3.shutdown();
    };
    private static volatile int i = 0;

    public static void main(String[] args) throws InterruptedException {
        executor1.submit(runnable);
    }
}
