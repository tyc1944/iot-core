package com.yunmo.iot.rpc;

import com.yunmo.iot.api.core.EntityEventService;
import com.yunmo.iot.domain.core.EntityEventRecord;
import com.yunmo.iot.domain.core.EntityEventRecordRpc;
import com.yunmo.iot.domain.core.GenericTelemetryRecordRpc;
import com.yunmo.iot.repository.cassandra.EntityEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class EntityEventServiceImpl implements EntityEventService {

    @Autowired
    EntityEventRepository entityEventRepository;

    @Override
    public List<EntityEventRecordRpc> queryTelemetryByDay(String entity,String channel, long time) {
        List<EntityEventRecord> entityEventRecords = entityEventRepository.findByDay(entity, channel,
                Instant.ofEpochSecond(time), 3600);
        List<EntityEventRecordRpc> result = new ArrayList<>();
        entityEventRecords.forEach(e -> {
            result.add(
                    EntityEventRecordRpc.builder()
                            .message(e.getMessage().toString())
                            .eventTime(e.getEventTime())
                            .entity(e.getEntity())
                            .channel(e.getChannel())
                            .deviceId(e.getDeviceId())
                            .build()
            );
        });
        return result;
    }
}
