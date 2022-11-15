package com.yunmo.iot.domain.app.repository;

import com.yunmo.domain.common.EntityRepository;
import com.yunmo.iot.domain.app.AppProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppProfileRepository extends EntityRepository<AppProfile, Long> {
    Page<AppProfile> findByTenantId(Long id, Pageable paging);
}
