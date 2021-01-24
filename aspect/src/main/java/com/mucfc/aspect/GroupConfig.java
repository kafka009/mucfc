package com.mucfc.aspect;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.BlockingQueue;

// javap -v -p ***.class
@Getter
@Setter
public class GroupConfig {
    private String name;
    private BlockingQueue<RppRunnable> queue;

    public boolean tryAcquire() {
        return true;
    }

    public void release() {
    }
}
