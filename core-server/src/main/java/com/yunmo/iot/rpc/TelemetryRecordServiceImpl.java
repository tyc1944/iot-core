package com.yunmo.iot.rpc;

import com.yunmo.iot.api.core.TelemetryRecordService;
import com.yunmo.iot.domain.core.GenericTelemetryRecord;
import com.yunmo.iot.domain.core.GenericTelemetryRecordRpc;
import com.yunmo.iot.repository.cassandra.TelemetryRecordC7aRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class TelemetryRecordServiceImpl implements TelemetryRecordService {

    @Autowired
    TelemetryRecordC7aRepository telemetryRecordC7aRepository;

    @Override
    public List<GenericTelemetryRecordRpc> listRance(long deviceId, Instant from, Instant to, String channel) {
        List<GenericTelemetryRecord> list = telemetryRecordC7aRepository.findByTimeRange(deviceId,channel,from,to);
        List<GenericTelemetryRecordRpc> result = new ArrayList<>();
        for (GenericTelemetryRecord genericTelemetryRecord : list) {
            GenericTelemetryRecordRpc recordRpc = GenericTelemetryRecordRpc.builder()
                    .eventTime(genericTelemetryRecord.getEventTime())
                    .channel(genericTelemetryRecord.getChannel())
                    .deviceId(genericTelemetryRecord.getDeviceId())
                    .entity(genericTelemetryRecord.getEntity())
                    .message(genericTelemetryRecord.getMessage().toString())
                    .build();
            result.add(recordRpc);
        }
        return result;
    }
}
