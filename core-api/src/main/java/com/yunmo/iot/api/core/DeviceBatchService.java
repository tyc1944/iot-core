package com.yunmo.iot.api.core;

import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.iot.domain.core.DeviceAuthorization;
import io.genrpc.annotation.GrpcService;

import java.util.List;

@GrpcService
public interface DeviceBatchService {
    List<DeviceAuthorization> createByLocalId(BatchByLocalId batch, @Principal Tenant tenant);
    List<DeviceAuthorization> createByCount(BatchByCount batch, @Principal Tenant tenant);
    List<DeviceAuthorization> getByBatch(String batch,long productId,
                                            long projectId,long hubId);
}
