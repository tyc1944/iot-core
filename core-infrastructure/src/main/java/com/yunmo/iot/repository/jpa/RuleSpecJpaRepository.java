package com.yunmo.iot.repository.jpa;

import com.yunmo.iot.domain.rule.RuleSpec;
import com.yunmo.iot.domain.rule.repository.RuleSpecRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RuleSpecJpaRepository extends JpaRepository<RuleSpec,Long>, RuleSpecRepository {
}
