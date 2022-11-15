package com.yunmo.iot.domain.event;

import com.yunmo.iot.domain.core.Device;
import com.yunmo.iot.domain.core.value.DeviceValue;
import io.genrpc.annotation.ProtoMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ProtoMessage
public class DeviceChangedEvent {
    public static final String TOPIC = "iot/domain/device-changed";
    private Long tenantId;
    private Long tenantPersonId;
    private Instant recordTime;
    /**
     * 设备对应的关联项目
     */
    private Long projectId;
    /**
     * 运维公司
     */
    private Long operatorId;
    private DeviceChangedType type;

    /**
     * 修改的属性信息
     */
    private Device changedDevice;
}

