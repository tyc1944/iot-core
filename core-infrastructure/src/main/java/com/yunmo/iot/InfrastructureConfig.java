package com.yunmo.iot;

import com.yunmo.iot.repository.cassandra.TelemetryRecordC7aRepository;
import com.yunmo.iot.repository.jpa.DeviceJpaRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableCassandraRepositories(basePackageClasses = TelemetryRecordC7aRepository.class)
@EnableJpaRepositories(basePackageClasses = DeviceJpaRepository.class)
public class InfrastructureConfig {
}
