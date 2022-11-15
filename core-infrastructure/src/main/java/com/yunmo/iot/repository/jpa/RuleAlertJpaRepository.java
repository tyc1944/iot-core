package com.yunmo.iot.repository.jpa;

import com.yunmo.iot.domain.rule.RuleAlert;
import com.yunmo.iot.domain.rule.repository.RuleAlertRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface RuleAlertJpaRepository extends JpaRepository<RuleAlert,Long>, RuleAlertRepository {

    long countByProjectIdAndAlertTimeGreaterThan(long projectId, Instant startOfDay);
}
