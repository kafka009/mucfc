package com.mucfc.aspect.config;

import java.util.Arrays;

public class ParamConfig extends AbstractConfigAble {
    public ParamConfig(ConfigEnum configEnum) {
        super(configEnum);
    }

    @Override
    protected boolean triggerOn() throws Exception {
        System.out.println("triggerOn......");
        return true;
    }

    @Override
    protected boolean triggerOff() throws Exception {
        System.out.println("triggerOff......");
        return true;
    }

    public static void main(String[] args) {
        ConfigAble config = new ParamConfig(ConfigEnum.ON);
        Arrays.stream(config.available()).forEach(System.out::println);
        System.out.println(config.on());
        System.out.println(config.off());
    }
}
