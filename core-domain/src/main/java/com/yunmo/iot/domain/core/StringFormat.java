package com.yunmo.iot.domain.core;

import com.yunmo.generator.annotation.JpaConverter;

@JpaConverter
public enum StringFormat {
    TEXT,
    JSON,
    HEX
}
