package com.mucfc.thread;

/**
 * 依赖于线程自旋对当前状态的判断
 */
public class JavaThreadNumber3 {
    private static volatile int i = 0;

    static class ThreadNumber extends Thread {
        private int index;

        public ThreadNumber(String name, int i) {
            this.setName(name);
            this.index = i;
        }

        public boolean myTime() {
            return i == index;
        }

        @Override
        public void run() {
            while (true) {
                if (!myTime()) {// 自旋:非我的场次慢慢等
                    Thread.yield();
                    continue;
                }
                if (i == 36) {
                    System.out.println(this.getName() + "exit.");
                    return;
                }
                System.out.println(this.getName() + ":" + (++i));
                System.out.println(this.getName() + ":" + (++i));
                System.out.println(this.getName() + ":" + (++i));// 自增和打印两个操作在多线程情况下会出问题
                index = Math.min(index + 9, 36);// 下一个属于我的任务的状态
                System.out.println(this.getName() + "下一个执行状态：" + index);
            }
        }
    }

    public static void main(String... args) {
        new ThreadNumber("work-1", 0).start();
        new ThreadNumber("work-2", 3).start();
        new ThreadNumber("work-3", 6).start();
    }
}
