package com.yunmo.iot.domain.core;

import com.yunmo.generator.annotation.JpaConverter;
import io.genrpc.annotation.ProtoEnum;

@ProtoEnum
@JpaConverter
public enum DeviceOperationStatus {
    MALFUNCTION, //故障
    NORMAL, //正常
    WARN //报警
}
