package com.yunmo.iot.domain.rule.repository;

import com.yunmo.domain.common.EntityRepository;
import com.yunmo.iot.domain.rule.CommandTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommandTemplateRepository extends EntityRepository<CommandTemplate, Long> {
    Page<CommandTemplate> findByProductId(Long productId, Pageable paging);
}
