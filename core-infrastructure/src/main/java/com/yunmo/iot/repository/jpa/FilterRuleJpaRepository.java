package com.yunmo.iot.repository.jpa;

import com.yunmo.iot.domain.rule.FilterRule;
import com.yunmo.iot.domain.rule.repository.FilterRuleRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilterRuleJpaRepository extends JpaRepository<FilterRule,Long>, FilterRuleRepository {
}
