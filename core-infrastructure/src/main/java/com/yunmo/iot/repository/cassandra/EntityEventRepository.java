package com.yunmo.iot.repository.cassandra;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatementBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.yunmo.iot.decode.Decoder;
import com.yunmo.iot.decode.DecoderFactory;
import com.yunmo.iot.decode.JsonRecordWriter;
import com.yunmo.iot.domain.core.EntityEventRecord;
import com.yunmo.iot.domain.core.MessageSchema;
import com.yunmo.iot.repository.jpa.MessageSchameJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.cql.SessionCallback;
import org.springframework.stereotype.Repository;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class EntityEventRepository {

    @Autowired
    CassandraTemplate cassandraTemplate;

    @Autowired
    MessageSchameJpaRepository schemaRepository;

    @Autowired
    DecoderFactory<JsonNode> decoderFactory;


    public List<EntityEventRecord> findByDay(String entity, String channel,Instant time,int limit){
        Instant from = time.atZone(ZoneId.systemDefault()).withHour(0).withMinute(0).withMinute(0).withNano(0).toInstant();
        Instant to = time.plus(1, ChronoUnit.DAYS).atZone(ZoneId.systemDefault()).withHour(0).withMinute(0).withMinute(0).withNano(0).toInstant();
        String  cql = "SELECT channel, entity, event_time, device_id, message " +
                "FROM iot.entity_events " +
                "where entity =? and channel = ? and event_time >= ? and event_time < ? " +
                "limit ?";
        SimpleStatementBuilder statementBuilder = new SimpleStatementBuilder(cql);
        statementBuilder.addPositionalValues(entity, channel, from, to, limit);

        return query( entity, channel, statementBuilder.build()).collect(Collectors.toList());
    }

    public List<EntityEventRecord> findByTimeRange(String entity, String channel, Instant from, Instant to, int limit) {

        // language=sql
        String  cql = "SELECT channel, entity, event_time, device_id, message " +
                "FROM iot.entity_events " +
                "where entity =? and channel = ? and event_time >= ? and event_time <= ? " +
                "limit ?";
        SimpleStatementBuilder statementBuilder = new SimpleStatementBuilder(cql);
        statementBuilder.addPositionalValues(entity, channel, from, to, limit);

        return query( entity, channel, statementBuilder.build()).collect(Collectors.toList());
    }




    private Function<Row, EntityEventRecord> entityEventMapper(String entity, Decoder decoder) {
        return row -> {
            String channel = row.getString("channel");
            long deviceId = row.getLong("device_id");
            ByteBuffer buffer = row.getByteBuffer("message");
            Instant eventTime = row.get("event_time", Instant.class);

            try {

                var message = Objects.isNull(buffer) ? null :
                        decoder.decode(buffer.array(), JsonRecordWriter.FACTORY);

                return new EntityEventRecord()
                        .setDeviceId(deviceId)
                        .setEntity(entity)
                        .setChannel(channel)
                        .setEventTime(eventTime)
                        .setMessage(message);

            } catch (Exception e) {
                return new EntityEventRecord()
                        .setEntity(entity)
                        .setDeviceId(deviceId)
                        .setChannel(channel)
                        .setEventTime(eventTime)
                        .setMessage(e.getMessage());
            }
        };

    }


    private Stream<EntityEventRecord> query(String entity, String channel, SimpleStatement statement) {
        Optional<MessageSchema> messageChannel = schemaRepository.findByChannelAndDeletedIsFalse(channel);
        Decoder decoder = messageChannel.map(c->
                decoderFactory.create(c.getRecordFormat(), c.getRecordSchema()))
                .orElse(decoderFactory.raw());
        return cassandraTemplate.getCqlOperations().execute((SessionCallback<List<EntityEventRecord>>) session ->
                session.execute(statement)
                        .all().stream().map(entityEventMapper(entity,decoder))
                        .collect(Collectors.toList())).stream();
    }
}
