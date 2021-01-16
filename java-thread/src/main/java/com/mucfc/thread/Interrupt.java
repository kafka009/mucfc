package com.mucfc.thread;

import java.io.IOException;
import java.io.InputStream;

public class Interrupt {
    public static void main(String[] args) throws InterruptedException {
        byte a = '1';
        byte b = '9';
        System.out.println((a + b - 2 * '0'));

        Thread thread = new Thread(() -> {
            while (!Thread.interrupted()) {
            }
            Thread.currentThread().interrupt();
        });
        thread.start();
        Thread.sleep(1000);
        System.out.println(thread.isInterrupted());
        thread.interrupt();
        Thread.sleep(1000);
        System.out.println(thread.isAlive() && thread.isInterrupted());

        Thread e = new Thread(() -> {
            InputStream ins = new InputStream() {
                @Override
                public int read() throws IOException {
                    while (true) {
                        if (false) break;
                    }
                    return 0;
                }
            };
            try {
                ins.read();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            try {
                Thread.sleep(30000L);
            } catch (InterruptedException x) {
                x.printStackTrace();
            }
            System.out.println("here");
        });
        e.start();
        e.interrupt();
    }
}
