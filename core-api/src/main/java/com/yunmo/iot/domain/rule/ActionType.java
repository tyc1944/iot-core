package com.yunmo.iot.domain.rule;

import com.yunmo.generator.annotation.JpaConverter;
import io.genrpc.annotation.ProtoEnum;

@ProtoEnum
@JpaConverter
public enum  ActionType {
    COMMAND_TO_TARGET_DEVICE,
    NOTIFICATION,
    BOTH
}
