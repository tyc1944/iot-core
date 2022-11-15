package com.yunmo.iot.domain.core.repository;

import com.yunmo.domain.common.EntityRepository;
import com.yunmo.iot.domain.core.DeviceConfigRecord;

import java.util.List;
import java.util.Optional;

public interface DeviceConfigRecordRepository extends EntityRepository<DeviceConfigRecord, DeviceConfigRecord.ID> {
    List<DeviceConfigRecord> findByDeviceIdAndVersionGreaterThanAndVersionGreaterThanEqual(Long deviceId, Long lastVersion, Long currentVersion);

    Optional<DeviceConfigRecord> findByDeviceIdAndVersion(Long deviceId, Long version);
}
