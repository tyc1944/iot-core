package com.yunmo.iot.domain.app;

import com.yunmo.generator.annotation.JpaConverter;

@JpaConverter
public enum AppType {
    CLIENT_PUSH,
    SERVER_SUB_PUB
}
