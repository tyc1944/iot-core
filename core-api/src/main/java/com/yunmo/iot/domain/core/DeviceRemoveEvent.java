package com.yunmo.iot.domain.core;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DeviceRemoveEvent {
    private Device device;
}
