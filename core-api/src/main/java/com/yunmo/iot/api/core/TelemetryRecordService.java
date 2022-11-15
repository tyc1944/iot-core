package com.yunmo.iot.api.core;

import com.yunmo.iot.domain.core.GenericTelemetryRecordRpc;
import io.genrpc.annotation.GrpcService;

import java.time.Instant;
import java.util.List;

@GrpcService
public interface TelemetryRecordService {

    List<GenericTelemetryRecordRpc> listRance(long deviceId, Instant from, Instant to, String channel);
}
