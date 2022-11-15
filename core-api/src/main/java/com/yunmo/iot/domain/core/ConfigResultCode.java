package com.yunmo.iot.domain.core;

import com.yunmo.generator.annotation.JpaConverter;
import io.genrpc.annotation.ProtoEnum;

@JpaConverter
@ProtoEnum
public enum ConfigResultCode {
    PENDING,
    OK,
    TIMEOUT,
    CONFLICT,
    OTHER,
    OVERRIDE
}
