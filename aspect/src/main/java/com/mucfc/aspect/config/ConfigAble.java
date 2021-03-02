package com.mucfc.aspect.config;

public interface ConfigAble {
    boolean on();

    boolean off();

    ConfigEnum[] available();
}
