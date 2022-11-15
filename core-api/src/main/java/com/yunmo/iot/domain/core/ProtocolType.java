package com.yunmo.iot.domain.core;

import com.yunmo.generator.annotation.JpaConverter;
import io.genrpc.annotation.ProtoEnum;

@ProtoEnum
@JpaConverter
public enum  ProtocolType {
    MQTT,
    COAP,
    HTTP
}
