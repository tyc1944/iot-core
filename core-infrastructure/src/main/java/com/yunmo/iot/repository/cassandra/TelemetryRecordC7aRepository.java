package com.yunmo.iot.repository.cassandra;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatementBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunmo.iot.decode.Decoder;
import com.yunmo.iot.decode.DecoderFactory;
import com.yunmo.iot.decode.JsonRecordWriter;
import com.yunmo.iot.domain.core.Device;
import com.yunmo.iot.domain.core.GenericTelemetryRecord;
import com.yunmo.iot.domain.core.GenericTelemetryRecordRpc;
import com.yunmo.iot.domain.core.MessageSchema;
import com.yunmo.iot.domain.core.repository.DeviceRepository;
import com.yunmo.iot.repository.jpa.MessageSchameJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.cql.SessionCallback;
import org.springframework.stereotype.Repository;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class TelemetryRecordC7aRepository {

    @Autowired
    CassandraTemplate cassandraTemplate;

    @Autowired
    MessageSchameJpaRepository schemaRepository;

    @Autowired
    DecoderFactory<JsonNode> decoderFactory;
    @Autowired
    DeviceRepository deviceRepository;

    public List<GenericTelemetryRecord> findByTimeRange(long deviceId, String channel, Instant from, Instant to) {

        // language=sql
        String  cql = "SELECT device_id,channel,event_time,message,entity " +
                "FROM iot.telemetry " +
                "where device_id=? and channel = ? and event_time >= ? and event_time <= ?";
        SimpleStatementBuilder statementBuilder = new SimpleStatementBuilder(cql);
        statementBuilder.addPositionalValues(deviceId, channel, from, to);
        Device device = deviceRepository.findById(deviceId).get();
        return query(deviceId, device.getProductId(), channel, statementBuilder.build()).collect(Collectors.toList());
    }


    public List<GenericTelemetryRecord> findByTimeRange(long deviceId, Long productId, String channel, Instant from, Instant to, int limit) {

        // language=sql
        String  cql = "SELECT device_id,channel,event_time,message,entity " +
                "FROM iot.telemetry " +
                "where device_id=? and channel = ? and event_time >= ? and event_time <= ?" +
                "limit ?";
        SimpleStatementBuilder statementBuilder = new SimpleStatementBuilder(cql);
        statementBuilder.addPositionalValues(deviceId, channel, from, to, limit);

        return query(deviceId, productId, channel, statementBuilder.build()).collect(Collectors.toList());
    }


    public Optional<GenericTelemetryRecord> findByTime(Long deviceId, Long productId, String channel, Instant time) {
        // language=sql
        String cql = "SELECT device_id,channel,event_time,message,entity " +
                "FROM iot.telemetry " +
                "where device_id=? and channel = ? and event_time = ?";

        SimpleStatementBuilder statementBuilder = new SimpleStatementBuilder(cql);
        statementBuilder.addPositionalValues(deviceId, channel, time);
        return query(deviceId, productId, channel, statementBuilder.build()).findFirst();
    }



    public Optional<GenericTelemetryRecord> findLatest(Long deviceId, Long productId, String channel) {
        // language=sql
        String cql = "SELECT device_id,channel,event_time,message,entity " +
                "FROM iot.telemetry " +
                "where device_id=? and channel = ? " +
                "ORDER BY event_time DESC LIMIT 1";

        SimpleStatementBuilder statementBuilder = new SimpleStatementBuilder(cql);
        statementBuilder.addPositionalValues(deviceId, channel);

        return query(deviceId, productId, channel, statementBuilder.build()).findFirst();
    }


    private Function<Row, GenericTelemetryRecord> telemetryRecordMapper(long deviceId, Decoder decoder) {
        return row -> {
            String channel = row.getString("channel");
            String entity = row.getString("entity");
            ByteBuffer buffer = row.getByteBuffer("message");
            Instant eventTime = row.get("event_time", Instant.class);

            try {

                var message = Objects.isNull(buffer) ? null :
                        decoder.decode(buffer.array(), JsonRecordWriter.FACTORY);

                return new GenericTelemetryRecord()
                        .setDeviceId(deviceId)
                        .setEntity(entity)
                        .setChannel(channel)
                        .setEventTime(eventTime)
                        .setMessage(message);

            } catch (Exception e) {
                return new GenericTelemetryRecord()
                        .setEntity(entity)
                        .setDeviceId(deviceId)
                        .setChannel(channel)
                        .setEventTime(eventTime)
                        .setMessage(e.getMessage());
            }
        };

    }


    private Stream<GenericTelemetryRecord> query(Long deviceId, Long productId, String channel, SimpleStatement statement) {
        Optional<MessageSchema> messageChannel = schemaRepository.findByProductIdAndChannel(productId,
                channel);
        Decoder decoder = messageChannel.map(c->
                decoderFactory.create(c.getRecordFormat(), c.getRecordSchema()))
                .orElse(decoderFactory.raw());
        return cassandraTemplate.getCqlOperations().execute((SessionCallback<List<GenericTelemetryRecord>>) session ->
                session.execute(statement)
                        .all().stream().map(telemetryRecordMapper(deviceId,decoder))
                        .collect(Collectors.toList())).stream();
    }
}
