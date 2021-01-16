package com.mucfc.thread;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;

/**
 * 依赖于每个线程自己的任务队列
 */
public class JavaThreadNumber5 {
    private static final TransferQueue<Runnable>[] deques = new LinkedTransferQueue[]{
            new LinkedTransferQueue<Runnable>(),
            new LinkedTransferQueue<Runnable>(),
            new LinkedTransferQueue<Runnable>()
    };
    private static final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + "=" + (++i));
            System.out.println(Thread.currentThread().getName() + "=" + (++i));
            System.out.println(Thread.currentThread().getName() + "=" + (++i));
        }
    };
    private static volatile int i = 0;

    static class ThreadNumber extends Thread {
        private final TransferQueue<Runnable> myQueue;
        private final TransferQueue<Runnable> nextQueue;
        private Thread next;

        @Override
        public void run() {
            try {
                while (true) {
                    Runnable runnable = this.myQueue.take();

                    runnable.run();

                    if (i == 36) {// 任务结束，回家
                        next.interrupt();
                        return;
                    }

                    produceToQueue();
                }
            } catch (InterruptedException e) {
                // Work done. Exit.
                next.interrupt();
            }
        }

        public void produceToQueue() throws InterruptedException {
            this.nextQueue.transfer(runnable);
        }

        public ThreadNumber(int i, TransferQueue<Runnable> myQeque, TransferQueue<Runnable> nextQueue) {
            this.setName("work-" + i);
            this.myQueue = myQeque;
            this.nextQueue = nextQueue;
        }

        public void setNext(Thread next) {
            this.next = next;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadNumber t1 = new ThreadNumber(1, deques[0], deques[1]);
        ThreadNumber t2 = new ThreadNumber(2, deques[1], deques[2]);
        ThreadNumber t3 = new ThreadNumber(3, deques[2], deques[0]);

        t1.setNext(t2);
        t2.setNext(t3);
        t3.setNext(t1);

        t1.start();
        t2.start();
        t3.start();

        deques[0].transfer(runnable);
    }
}
