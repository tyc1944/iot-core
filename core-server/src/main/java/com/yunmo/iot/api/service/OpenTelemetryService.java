package com.yunmo.iot.api.service;

import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.iot.domain.core.Device;
import com.yunmo.iot.domain.core.GenericTelemetryRecord;
import com.yunmo.iot.pipe.core.Topics;
import com.yunmo.iot.repository.cassandra.TelemetryRecordC7aRepository;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/open/devices")
@PreAuthorize("hasRole('OPEN')")
public class OpenTelemetryService {

    @Autowired
    TelemetryRecordC7aRepository telemetryRecordRepository;

    @PreAuthorize("hasRole('OPEN')")
    @GetMapping("/{id}/telemetries")
    public List<GenericTelemetryRecord> queryTelemetry( @PathVariable("id") Device device,
                                                       @RequestParam(required = false, defaultValue = Topics.DEFAULT_CHANNEL) String channel,
                                                       @ApiParam("时间戳(秒)") @RequestParam long from,
                                                       @ApiParam("时间戳(秒)") @RequestParam(required = false) Optional<Long> to) {
        return telemetryRecordRepository.findByTimeRange(device.getId(), device.getProductId(),
                channel, Instant.ofEpochSecond(from), to.map(Instant::ofEpochSecond).orElse(Instant.now()), 3600);
    }

    @PreAuthorize("hasRole('OPEN')")
    @GetMapping("/{id}/telemetries/latest")
    public Optional<GenericTelemetryRecord> queryLatestTelemetry(@PathVariable("id") Device device,
                                                                 @RequestParam(required = false, defaultValue = Topics.DEFAULT_CHANNEL) String channel) {
        return telemetryRecordRepository.findLatest(device.getId(), device.getProductId(),channel);
    }
}
