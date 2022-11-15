package com.yunmo.iot.domain.core;

import com.yunmo.generator.annotation.JpaConverter;
import io.genrpc.annotation.ProtoEnum;
import io.genrpc.annotation.ProtoMessage;

@ProtoEnum
@JpaConverter
public enum DeviceConnectionStatus {
    ONLINE, //在线
    OFFLINE, //离线
    INACTIVE //未激活
}
