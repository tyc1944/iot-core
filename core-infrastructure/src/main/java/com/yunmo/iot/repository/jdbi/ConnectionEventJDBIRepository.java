package com.yunmo.iot.repository.jdbi;

import com.yunmo.iot.domain.core.Device;
import com.yunmo.iot.pipe.core.DeviceOfflineEvent;
import com.yunmo.iot.pipe.core.DeviceOnlineEvent;
import com.yunmo.iot.repository.jdbi.values.IntervalUnit;
import com.yunmo.iot.repository.jdbi.values.OnlineOfflineStats;
import com.yunmo.iot.repository.jdbi.values.ProjectOverviewStats;
import com.yunmo.iot.repository.jpa.DeviceCommandRecordJpaRepository;
import com.yunmo.iot.repository.jpa.DeviceJpaRepository;
import com.yunmo.iot.repository.jpa.RuleAlertJpaRepository;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ConnectionEventJDBIRepository  {
    @Autowired
    Jdbi clickHouseJDBI;

    @Autowired
    DeviceJpaRepository deviceJpaRepository;

    @Autowired
    DeviceCommandRecordJpaRepository deviceCommandRecordJpaRepository;

    @Autowired
    RuleAlertJpaRepository ruleAlertJpaRepository;

    @EventListener
    public void logOnline(DeviceOnlineEvent event) {
        Device device = deviceJpaRepository.findById(event.getDeviceId()).get();
        logConnectLog(device.getProjectId(), event.getDeviceId(), event.getTs(), 1);
    }

    @EventListener
    public void logOffline(DeviceOfflineEvent event) {
        Device device = deviceJpaRepository.findById(event.getDeviceId()).get();
        logConnectLog(device.getProjectId(), event.getDeviceId(), event.getTs(), 0);
    }

    public void logConnectLog(long projectId, long deviceId, Instant ts, int online) {
        clickHouseJDBI.withHandle(handle ->
                handle.execute("INSERT INTO iot.connect_log VALUES (?,?,?,?)",
                        projectId, deviceId,ts.getEpochSecond(), online ));
    }

    public List<OnlineOfflineStats> periodCounts(long projectId, IntervalUnit interval, Instant from, Instant to) {
        return clickHouseJDBI.withHandle(handle ->
                handle.createQuery("select toStartOfInterval(hourlyTime, INTERVAL 1 " + interval.name() + ") as intervalTime, " +
                                "sumMerge(online) as online, countMerge(total) - online as offline " +
                                "from iot.connect_log_hourly_mv " +
                                "where projectId = :projectId and hourlyTime >= :from and hourlyTime <= :to " +
                                "GROUP BY intervalTime ORDER BY intervalTime;")
                        .bind("projectId", projectId)
                        .bind("from", from.getEpochSecond())
                        .bind("to", to.getEpochSecond())
                .mapToBean(OnlineOfflineStats.class).collect(Collectors.toList())
        );
    }

    public ProjectOverviewStats projectOverviewStats(long projectId, ZoneId zoneId) {
        Instant startOfDay = LocalDate.now().atStartOfDay(zoneId).toInstant();
        return ProjectOverviewStats.builder()
                .deviceCount(deviceJpaRepository.countByProjectId(projectId))
                .alertCountToday(ruleAlertJpaRepository.countByProjectIdAndAlertTimeGreaterThan(projectId, startOfDay))
                .commandCountToday(deviceCommandRecordJpaRepository.countByProjectIdAndCreatedTimeGreaterThan(projectId, startOfDay))
                .build();
    }
}
