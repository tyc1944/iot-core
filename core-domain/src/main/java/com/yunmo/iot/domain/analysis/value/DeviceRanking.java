package com.yunmo.iot.domain.analysis.value;

import com.yunmo.iot.domain.core.Device;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
public class DeviceRanking {
    private long __project_id;
    private long __device_id;
    private Device device;
    private Date __date;
    private Double value;
}
