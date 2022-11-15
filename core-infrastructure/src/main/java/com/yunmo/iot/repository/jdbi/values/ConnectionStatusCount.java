package com.yunmo.iot.repository.jdbi.values;

import com.yunmo.iot.domain.core.DeviceConnectionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionStatusCount {
    private DeviceConnectionStatus status;
    private long count;
}
