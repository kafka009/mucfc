package com.mucfc.thread;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class Interrupt {
    public static void main(String[] args) {
        BlockingDeque<Integer> deque = new LinkedBlockingDeque<>(2);
        System.out.println(Thread.currentThread().isInterrupted());
        Thread.currentThread().interrupt();
        System.out.println(Thread.currentThread().isInterrupted());
        try {
            deque.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().isInterrupted());
        try {
            deque.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // BUG
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ignore) {
            }
        }

        // OK
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
            }
        }

        while (true) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                Runtime.getRuntime().exit(0);
            }
        }
    }
}
