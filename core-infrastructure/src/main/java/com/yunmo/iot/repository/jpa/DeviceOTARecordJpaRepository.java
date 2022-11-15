package com.yunmo.iot.repository.jpa;

import com.yunmo.iot.domain.ota.DeviceOTARecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceOTARecordJpaRepository extends JpaRepository<DeviceOTARecord, Long> {
    Page<DeviceOTARecord> findByDeviceId(Long deviceId, Pageable paging);
    DeviceOTARecord findFirstByDeviceIdOrderByCreatedDate(long deviceId);
}
