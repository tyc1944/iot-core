package com.yunmo.iot.repository.jpa;

import com.yunmo.iot.domain.rule.CommandTemplate;
import com.yunmo.iot.domain.rule.repository.CommandTemplateRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandTemplateJpaRepository extends JpaRepository<CommandTemplate,Long>, CommandTemplateRepository {
}
