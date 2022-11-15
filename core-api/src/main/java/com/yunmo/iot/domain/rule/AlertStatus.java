package com.yunmo.iot.domain.rule;

import com.yunmo.generator.annotation.JpaConverter;
import io.genrpc.annotation.ProtoEnum;

@ProtoEnum
@JpaConverter
public enum AlertStatus {
    TRIGGER, //触发
    CLEAR  //解除
}
