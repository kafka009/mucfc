package com.mucfc.aspect.config;

import static com.mucfc.aspect.config.ConfigEnum.OFF;
import static com.mucfc.aspect.config.ConfigEnum.ON;

public abstract class AbstractConfigAble implements ConfigAble {
    private ConfigEnum configEnum;

    public AbstractConfigAble(ConfigEnum configEnum) {
        this.configEnum = configEnum;
    }

    @Override
    public boolean on() {
        if (!configEnum.available(ON)) {
            return false;
        }

        ConfigEnum keep = configEnum;
        try {
            return triggerOn();
        } catch (Exception e) {
            e.printStackTrace();

            // rollback
            configEnum = keep;
            return false;
        }
    }

    @Override
    public boolean off() {
        if (!configEnum.available(OFF)) {
            return false;
        }

        ConfigEnum keep = configEnum;
        try {
            return triggerOff();
        } catch (Exception e) {
            e.printStackTrace();

            // rollback
            configEnum = keep;
            return false;
        }
    }

    protected abstract boolean triggerOn() throws Exception;

    protected abstract boolean triggerOff() throws Exception;

    @Override
    public ConfigEnum[] available() {
        return configEnum.availableConfig();
    }
}
