package com.yunmo.iot.domain.core;

import io.genrpc.annotation.ProtoMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Instant;

@Data
@Accessors(chain = true)
@ProtoMessage
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenericTelemetryRecordRpc {

    private Long deviceId;
    private String channel;

    private Instant eventTime;
    private String entity;
    private String message;
}
