package com.yunmo.iot.domain.core;

import io.genrpc.annotation.ProtoMessage;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.Map;

@Data
@Accessors(chain = true)
@ProtoMessage
public class DeviceState {
    private Instant timestamp;
    private int version;
    private ConfigResultCode result;
    private Map<String,Object> content;
}
