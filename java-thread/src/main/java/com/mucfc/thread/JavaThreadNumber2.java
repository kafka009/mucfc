package com.mucfc.thread;

import java.util.concurrent.Semaphore;

/**
 * 依赖于上一个任务释放信号量
 */
public class JavaThreadNumber2 {
    private static volatile int i = 0;

    static class ThreadNumber implements Runnable {
        private final String work;
        private final Semaphore a;
        private final Semaphore b;

        public ThreadNumber(int work, Semaphore a, Semaphore b) {
            this.work = "work-" + work;
            this.a = a;
            this.b = b;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    a.acquire();
                    if (i == 36) {
                        b.release();
                        return;
                    }
                    System.out.println(work + "=" + (++i));
                    System.out.println(work + "=" + (++i));
                    System.out.println(work + "=" + (++i));
                    b.release();
                }
            } catch (InterruptedException ignore) {
                // Never here.
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Semaphore a = new Semaphore(1);
        Semaphore b = new Semaphore(1);
        Semaphore c = new Semaphore(1);
        a.acquire(1);
        b.acquire(1);
        c.acquire(1);
        new Thread(new ThreadNumber(1, a, b)).start();
        new Thread(new ThreadNumber(2, b, c)).start();
        new Thread(new ThreadNumber(3, c, a)).start();
        a.release();
    }
}
