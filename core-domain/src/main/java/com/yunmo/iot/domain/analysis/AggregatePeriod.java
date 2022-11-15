package com.yunmo.iot.domain.analysis;

import com.yunmo.generator.annotation.JpaConverter;

@JpaConverter
public enum AggregatePeriod {
    HOURLY,
    DAILY,
    WEEKLY,
    MONTHLY,
    QUARTERLY,
    YEARLY
}
