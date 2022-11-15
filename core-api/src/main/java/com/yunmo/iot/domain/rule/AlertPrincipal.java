package com.yunmo.iot.domain.rule;

import com.yunmo.generator.annotation.JpaConverter;
import io.genrpc.annotation.ProtoEnum;

@ProtoEnum
@JpaConverter
public enum AlertPrincipal {
    DEVICE,
    THING
}
