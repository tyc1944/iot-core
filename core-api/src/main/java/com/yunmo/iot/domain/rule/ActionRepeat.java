package com.yunmo.iot.domain.rule;

import com.yunmo.generator.annotation.JpaConverter;
import io.genrpc.annotation.ProtoEnum;

@ProtoEnum
@JpaConverter
public enum ActionRepeat {
    ONCE,
    EVERY_HOUR,
    EVERY_DAY,
    WORK_DAY,
    EVERY_WEEK,
    EVERY_MONTH,
    CUSTOM_DAYS_OF_WEEK
}
