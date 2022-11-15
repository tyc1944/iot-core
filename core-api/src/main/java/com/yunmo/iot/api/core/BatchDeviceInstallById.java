package com.yunmo.iot.api.core;

import com.google.common.base.Objects;
import com.yunmo.iot.domain.core.value.DeviceInstall;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * @author lh
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class BatchDeviceInstallById extends DeviceInstall {
    private Long id;
}
