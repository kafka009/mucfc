package com.mucfc.thread;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 依赖于lock.condition之间的相互唤醒
 */
public class JavaThreadNumber4 {
    private static volatile int i = 0;
    private static final Lock lock = new ReentrantLock();

    static class ThreadNumber extends Thread {
        private final Condition a;
        private final Condition b;
        private volatile boolean await = false;

        public ThreadNumber(int work, Condition a, Condition b) {
            this.setName("work-" + work);
            this.a = a;
            this.b = b;
        }

        public boolean isAwait() {
            return this.await;
        }

        @Override
        public void run() {
            lock.lock();
            try {
                await = true;// 进入了lock代码块
                synchronized (lock) {
                    lock.notify();
                }
                while (true) {
                    a.await();// 释放锁
                    if (i == 36) {
                        b.signal();
                        return;
                    }
                    System.out.println(this.getName() + "=" + (++i));
                    System.out.println(this.getName() + "=" + (++i));
                    System.out.println(this.getName() + "=" + (++i));
                    b.signal();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Condition a = lock.newCondition();
        Condition b = lock.newCondition();
        Condition c = lock.newCondition();

        ThreadNumber t1 = new ThreadNumber(1, a, b);
        ThreadNumber t2 = new ThreadNumber(2, b, c);
        ThreadNumber t3 = new ThreadNumber(3, c, a);

        t1.start();
        t2.start();
        t3.start();

        // 线程还没有拿到锁，循环
        synchronized (lock) {
            while (!t1.isAwait() || !t2.isAwait() || !t3.isAwait()) {
                lock.wait();
            }
        }

        // t1已经拿到过锁
        System.out.println("==========");
        lock.lock();
        // t1已经释放锁
        try {
            // 唤醒t1，让t1执行
            a.signal();
        } finally {
            lock.unlock();
        }
    }
}
