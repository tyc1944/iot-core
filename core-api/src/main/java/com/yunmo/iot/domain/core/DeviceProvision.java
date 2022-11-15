package com.yunmo.iot.domain.core;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author lh
 */
@Data
@Builder
@Accessors(chain = true)
public class DeviceProvision {
    private Device device;
    private DeviceAuthorization deviceAuthorization;
}
