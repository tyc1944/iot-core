package com.yunmo.iot.repository.jpa;

import com.yunmo.iot.domain.rule.GeoFence;
import com.yunmo.iot.domain.rule.repository.GeoFenceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeoFenceJpaRepository extends JpaRepository<GeoFence, Long>, GeoFenceRepository {
    Page<GeoFence> findByTenantIdAndProjectId(Long tenantId, Long projectId, Pageable paging);
}
