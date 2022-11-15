package com.yunmo.iot.domain.rule.repository;

import com.yunmo.domain.common.EntityRepository;
import com.yunmo.iot.domain.rule.TimerAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TimerActionRepository extends EntityRepository<TimerAction, Long> {
    Page<TimerAction> findByTenantId(Long id, Pageable paging);
}
