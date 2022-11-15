package com.yunmo.iot.repository.jpa;

import com.yunmo.iot.domain.core.DeviceCommandRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface DeviceCommandRecordJpaRepository extends JpaRepository<DeviceCommandRecord, Long> {
    Page<DeviceCommandRecord> findAllByDeviceId(Long id, Pageable paging);
    long countByProjectIdAndCreatedTimeGreaterThan(long projectId, Instant startOfDay);
}
