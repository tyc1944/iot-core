package com.yunmo.iot.domain.core;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;

@Data
@Accessors(chain = true)
public class GenericTelemetryRecord {
    private Long deviceId;
    private String channel;

    private Instant eventTime;

    private Object message;

    private String entity;
}
