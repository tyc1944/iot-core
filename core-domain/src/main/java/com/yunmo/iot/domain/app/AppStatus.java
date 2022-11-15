package com.yunmo.iot.domain.app;

import com.yunmo.generator.annotation.JpaConverter;

@JpaConverter
public enum AppStatus {
    NORMAL,
    DISABLED
}
