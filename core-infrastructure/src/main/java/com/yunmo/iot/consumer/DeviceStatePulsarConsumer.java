package com.yunmo.iot.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.CodedInputStream;
import com.yunmo.domain.common.Events;
import com.yunmo.iot.domain.core.DeviceState;
import com.yunmo.iot.domain.core.DeviceStateReportEvent;
import com.yunmo.iot.pipe.core.DeviceBasicMessage;
import com.yunmo.iot.pipe.core.DeviceBasicMessageMarshaller;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

@Component
@Slf4j
public class DeviceStatePulsarConsumer {
    @Autowired
    private PulsarClient client;

    ConsumerBuilder<byte[]> consumerBuilder;

    private DeviceBasicMessageMarshaller marshaller = new DeviceBasicMessageMarshaller();

    ObjectMapper objectMapper = new ObjectMapper();

    @EventListener
    public void run(ContextRefreshedEvent event) {
        this.consumerBuilder = this.client.newConsumer()
                .topic("iot/pipe/state")
                .subscriptionName("iot-core-state")
                .deadLetterPolicy(DeadLetterPolicy.builder()
                .maxRedeliverCount(3)
                .build())
                .subscriptionType(SubscriptionType.Shared);
        IntStream.range(0,4).forEach(i->{
            try {
                this.consumerBuilder.consumerName(String.format("iot-core-state-consumer-%d", i))
                        .messageListener(this::process)
                        .subscribe();
            } catch (PulsarClientException e) {
                log.error("状态订阅失败：{}", e.getMessage());
            }
        });
    }

    private void process(Consumer<byte[]> consumer, Message<byte[]> msg) {
        try {
            DeviceBasicMessage deviceMessage = marshaller.parse(CodedInputStream.newInstance(msg.getData()), null);
            String msgStr = deviceMessage.getMessage().toStringUtf8();
            DeviceState state = objectMapper.readValue(msgStr, DeviceState.class);
            Events.post(new DeviceStateReportEvent().setDeviceId(deviceMessage.getDeviceId()).setState(state));
            consumer.acknowledge(msg);
        } catch (Exception e) {
            consumer.negativeAcknowledge(msg);
        }
    }
}
