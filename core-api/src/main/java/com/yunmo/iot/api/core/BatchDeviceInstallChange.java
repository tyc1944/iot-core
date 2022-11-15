package com.yunmo.iot.api.core;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Set;

/**
 * @author lh
 */
@Getter
@Setter
@Accessors(chain = true)
public class BatchDeviceInstallChange {
    private Set<BatchDeviceInstallById> batchDeviceInstallByIds;

    private Set<Long> unInstallDeviceIds;
}
