package com.yunmo.iot.api.core;

import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.iot.domain.core.Device;
import com.yunmo.iot.domain.core.GenericTelemetryRecord;
import com.yunmo.iot.pipe.core.Topics;
import com.yunmo.iot.repository.cassandra.TelemetryRecordC7aRepository;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/devices")
public class DeviceTelemetryResource  extends DeviceResourceBase {

    @Autowired
    TelemetryRecordC7aRepository telemetryRecordRepository;

    @GetMapping("/{id}/telemetries")
    public List<GenericTelemetryRecord> queryTelemetry(@Principal Tenant tenant, @PathVariable("id") Device device,
                                                       @RequestParam(required = false, defaultValue = Topics.DEFAULT_CHANNEL) String channel,
                                                       @ApiParam("时间戳") @RequestParam long from,
                                                       @ApiParam("时间戳") @RequestParam(required = false) Optional<Long> to,
                                                       @ApiParam("最大数据量") @RequestParam(required = false) Optional<Integer> limit) {
        checkWithProject(device, tenant);
        return telemetryRecordRepository.findByTimeRange(device.getId(), device.getProductId(),
                channel, Instant.ofEpochSecond(from), to.map(Instant::ofEpochSecond).orElse(Instant.now()), limit.orElse(3600));
    }

    @GetMapping("/{id}/telemetries/{time}")
    public Optional<GenericTelemetryRecord> queryTelemetry(@Principal Tenant tenant, @PathVariable("id") Device device,
                                                           @RequestParam(required = false, defaultValue = Topics.DEFAULT_CHANNEL) String channel,
                                                           @ApiParam("时间戳") @PathVariable long time) {
        checkWithProject(device, tenant);
        return telemetryRecordRepository.findByTime(device.getId(), device.getProductId(),
                channel, Instant.ofEpochMilli(time));
    }

    @GetMapping("/{id}/telemetries/latest")
    public Optional<GenericTelemetryRecord> queryLatestTelemetry(@Principal Tenant tenant, @PathVariable("id") Device device,
                                                           @RequestParam(required = false, defaultValue = Topics.DEFAULT_CHANNEL) String channel) {
        checkWithProject(device, tenant);
        return telemetryRecordRepository.findLatest(device.getId(), device.getProductId(),channel);
    }
}
