package com.yunmo.iot.api.core;

import com.yunmo.iot.domain.core.EntityEventRecordRpc;
import io.genrpc.annotation.GrpcService;

import java.util.List;

@GrpcService
public interface EntityEventService {

    List<EntityEventRecordRpc> queryTelemetryByDay(String entity,String channel, long time);
}
