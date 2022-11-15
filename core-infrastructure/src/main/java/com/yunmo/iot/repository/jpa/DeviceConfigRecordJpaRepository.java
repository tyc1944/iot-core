package com.yunmo.iot.repository.jpa;

import com.yunmo.iot.domain.core.DeviceConfigRecord;
import com.yunmo.iot.domain.core.repository.DeviceConfigRecordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceConfigRecordJpaRepository extends JpaRepository<DeviceConfigRecord, DeviceConfigRecord.ID>, DeviceConfigRecordRepository {
    Page<DeviceConfigRecord> findAllByDeviceId(Long id, Pageable paging);
}
