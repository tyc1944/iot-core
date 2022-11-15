package com.yunmo.iot.domain.core;

import com.yunmo.generator.annotation.JpaConverter;

@JpaConverter
public enum IndustryType {
    SOLAR,//光伏
    AGRICULTURE,//农业
    MANUFACTURE,//制造业
    OTHER
}
