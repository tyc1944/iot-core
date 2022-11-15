package com.yunmo.iot.domain.rule.repository;

import com.yunmo.domain.common.EntityRepository;
import com.yunmo.iot.domain.rule.RuleSpec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RuleSpecRepository extends EntityRepository<RuleSpec, Long> {
    Page<RuleSpec> findByTenantId(Long tenantId, Pageable paging);

    Page<RuleSpec> findByDeviceId(Long deviceId, Pageable paging);

    Page<RuleSpec> findByProductId(Long productId, Pageable paging);
}
