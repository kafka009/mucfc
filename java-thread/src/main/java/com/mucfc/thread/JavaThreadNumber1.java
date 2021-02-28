package com.mucfc.thread;

import java.util.concurrent.Semaphore;

public class JavaThreadNumber1 {
    private static volatile int i = 0;

    static class ThreadNumber implements Runnable {
        private final String work;
        private Thread prev;
        private Thread self;
        private boolean init = false;

        public ThreadNumber(int work) {
            this.work = "work-" + work;
        }

        public void setPrev(Thread prev) {
            this.prev = prev;
        }

        public void setSelf(Thread self) {
            this.self = self;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    synchronized (prev) {
                        if (!init) {
                            prev.notifyAll();
                            prev.wait();
                        }
                        synchronized (self) {
                            if (!init) {
                                init = true;
                                self.notifyAll();
                                self.wait();
                            }
                            if (i == 36) {
                                prev.interrupt();
                                return;
                            }

                            doWork();
                            self.notifyAll();
                        }
                        prev.wait();
                    }
                }
            } catch (InterruptedException ignore) {
                // Never here.
                prev.interrupt();
            }
        }

        private void doWork() {
            System.out.println(work + "=" + (++i));
            System.out.println(work + "=" + (++i));
            System.out.println(work + "=" + (++i));
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadNumber n1 = new ThreadNumber(1);
        ThreadNumber n2 = new ThreadNumber(2);
        ThreadNumber n3 = new ThreadNumber(3);

        Thread t1 = new Thread(n1);
        Thread t2 = new Thread(n2);
        Thread t3 = new Thread(n3);

        n1.setPrev(t3);
        n1.setSelf(t1);
        n2.setPrev(t1);
        n2.setSelf(t2);
        n3.setPrev(t2);
        n3.setSelf(t3);

        synchronized (t1) {
            synchronized (t2) {
                synchronized (t3) {
                    t1.start();
                    t2.start();
                    t3.start();

                    t3.wait();
                    t3.notifyAll();
                }
                t1.wait();
                t1.notifyAll();
            }
            t1.wait();
        }
    }
}
