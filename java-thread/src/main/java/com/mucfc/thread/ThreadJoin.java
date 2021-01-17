package com.mucfc.thread;

public class ThreadJoin {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                System.out.println("start");
                Thread.sleep(1000L);
                System.out.println("end");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        thread.join();
        synchronized (Thread.currentThread()) {
            Thread.currentThread().wait(2000L);
        }
        System.out.println("here");
    }
}
