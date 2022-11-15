package com.yunmo.iot.api.core;

import com.yunmo.iot.domain.ota.DeviceOTARecord;
import io.genrpc.annotation.GrpcService;

@GrpcService
public interface DeviceOtaRecordService {

    DeviceOTARecord getLastByDeviceId(long deviceId);
}
