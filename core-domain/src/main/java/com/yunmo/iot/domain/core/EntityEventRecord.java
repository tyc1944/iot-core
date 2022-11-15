package com.yunmo.iot.domain.core;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;

@Data
@Accessors(chain = true)
public class EntityEventRecord {
    private String entity;
    private String channel;
    private Instant eventTime;
    private long deviceId;
    private Object message;
}
