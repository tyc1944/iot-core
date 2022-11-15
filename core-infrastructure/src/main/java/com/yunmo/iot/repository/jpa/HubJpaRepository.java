package com.yunmo.iot.repository.jpa;

import com.yunmo.iot.domain.core.DeviceHub;
import com.yunmo.iot.domain.core.repository.DeviceHubRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HubJpaRepository extends JpaRepository<DeviceHub, Long>, DeviceHubRepository {
    Page<DeviceHub> findAllByProductId(Long id, Pageable paging);
    List<DeviceHub> findAllByProductId(Long id);
    DeviceHub findFirstByProductId(Long id);
}
