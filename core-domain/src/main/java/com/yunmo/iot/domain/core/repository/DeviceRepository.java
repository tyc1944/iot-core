package com.yunmo.iot.domain.core.repository;

import com.yunmo.domain.common.EntityRepository;
import com.yunmo.iot.domain.core.Device;
import com.yunmo.iot.domain.core.DeviceConnectionStatus;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface DeviceRepository  extends EntityRepository<Device, Long> {
    List<Device> findByIdInAndLocalIdIn(Collection<Long> ids, Collection<String> localIds);

    void online(List<Long> ids, Instant time);
    void offline(List<Long> ids, Instant time);
    void setOnlineStatus(List<Long> ids, DeviceConnectionStatus status);

    Long countByProjectId(Long id);

    Device findByProductIdAndLocalId(long productId, String localId);

    List<Device> findByProjectId(long projectId);

    Device findByLocalId(String localId);

    List<Device> findByProductIdAndProjectIdAndBatch(long productId,long projectId,String batch);

    Device findByProductIdAndLocalIdAndDeviceStatus(long productId, String localId, DeviceConnectionStatus status);

    List<Device> findByConfigContentProperty(String key,String value);

    List<Device> findByAttributesProperty(String key,String value);

}
