package com.yunmo.iot.domain.core;

import lombok.Data;

import java.util.Set;

@Data
public class DeviceBatchConfig {

    private Set<Long> deviceIds;

    private DeviceCommands command;

    @Data
    public static class DeviceCommands {
        private String channel;

        private String format;

        private String content;
    }

}
