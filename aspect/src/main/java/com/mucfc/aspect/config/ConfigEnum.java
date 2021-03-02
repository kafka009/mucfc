package com.mucfc.aspect.config;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public enum ConfigEnum {
    ON("ON", "开启", "OFF"),
    OFF("OFF", "关闭", "ON");

    private final String code;
    private final String desc;
    private final String[] available;

    private volatile ConfigEnum[] availableConfig;
    private static final Map<String, ConfigEnum> CACHE = new LinkedHashMap<>(4);

    static {
        for (ConfigEnum value : ConfigEnum.values()) {
            CACHE.put(value.code, value);
        }
    }

    ConfigEnum(String code, String desc, String... available) {
        this.code = code;
        this.desc = desc;
        this.available = available;
    }

    public String code() {
        return code;
    }

    public String desc() {
        return desc;
    }

    public static ConfigEnum of(String code) {
        return CACHE.get(code);
    }

    public ConfigEnum[] availableConfig() {
        if (null != availableConfig) {
            return availableConfig;
        }
        return availableConfig = Arrays.stream(available).map(ConfigEnum::of).toArray(ConfigEnum[]::new);
    }

    public boolean available(ConfigEnum targetEnum) {
        for (ConfigEnum configEnum : availableConfig()) {
            if (targetEnum == configEnum) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        Arrays.stream(ON.availableConfig()).forEach(System.out::println);
    }
}
