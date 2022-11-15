package com.yunmo.iot.api.core;

import com.yunmo.iot.domain.core.Device;
import com.yunmo.iot.domain.core.DeviceHub;
import com.yunmo.iot.domain.core.value.DeviceValue;
import io.genrpc.annotation.GrpcService;

import java.util.List;

@GrpcService
public interface DeviceService {
    Device create(DeviceValue value);
    Device save(Device device);
    void saveAll(List<Device> devices);
    Device getDeviceById(Long id);
    void setOnlineStatus(long id, boolean status);
    Device getDeviceByLocalId(long productId, String localId);
    Device autoProvisionSubDevice(long gatewayId, String localId);
    DeviceHub getHubById(Long id);
    List<Device> getGatewayChildren(Long gatewayId);
    List<Device> getByProjectId(long projectId);
    List<Device> findByConfigContentProperty(String key,String value);
    List<Device> findByAttributesProperty(String key,String value);
    List<Device> findAllInIds(List<Long> ids);
}
