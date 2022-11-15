package com.yunmo.iot.repository.jdbi;

import com.yunmo.generator.annotation.JDBI;
import com.yunmo.iot.repository.jdbi.values.AlertCountStats;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;

import java.time.Instant;
import java.util.List;

@JDBI
public interface AlertsCountStatsQuery  {
    @RegisterBeanMapper(AlertCountStats.class)
    List<AlertCountStats> statsOfProject(Long projectId, Instant timeStart, Instant timeEnd);

    @RegisterBeanMapper(AlertCountStats.class)
    List<AlertCountStats> statsOfProduct(Long productId, Instant timeStart, Instant timeEnd);
}
