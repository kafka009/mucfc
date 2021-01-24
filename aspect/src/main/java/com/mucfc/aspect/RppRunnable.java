package com.mucfc.aspect;

import lombok.AllArgsConstructor;

import java.util.concurrent.Callable;

@AllArgsConstructor
public abstract class RppRunnable implements Runnable {
    private final GroupConfig groupConfig;
    private final Runnable runnable;

    public GroupConfig getGroupConfig() {
        return groupConfig;
    }

    public void run() {
        runnable.run();
    }
}
