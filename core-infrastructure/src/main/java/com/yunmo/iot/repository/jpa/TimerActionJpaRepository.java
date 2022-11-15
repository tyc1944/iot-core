package com.yunmo.iot.repository.jpa;

import com.yunmo.iot.domain.rule.TimerAction;
import com.yunmo.iot.domain.rule.repository.TimerActionRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimerActionJpaRepository extends JpaRepository<TimerAction, Long>, TimerActionRepository {
}
