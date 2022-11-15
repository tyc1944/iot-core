package com.yunmo.iot.consumer;

import com.google.protobuf.CodedInputStream;
import com.yunmo.domain.common.Events;
import com.yunmo.iot.domain.rule.TimerAction;
import com.yunmo.iot.domain.rule.TimerActionMarshaller;
import com.yunmo.iot.domain.rule.service.TimerActionSchedulerService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.IntStream;

@Component
@Slf4j
public class TimerActionSchedulerQueue {
    @Autowired
    private PulsarClient client;


    private TimerActionMarshaller marshaller = new TimerActionMarshaller();
    private Consumer<byte[]> consumer;
    private Producer<byte[]> producer;

    public static final String QUEUE_TOPIC = "iot/rule/actions";

    @SneakyThrows
    @EventListener
    public void run(ContextRefreshedEvent event) {
        ConsumerBuilder<byte[]> consumerBuilder = this.client.newConsumer()
                .topic(QUEUE_TOPIC)
                .subscriptionName("iot-core")
                .deadLetterPolicy(DeadLetterPolicy.builder()
                        .maxRedeliverCount(3)
                        .build())
                .subscriptionType(SubscriptionType.Shared);
        IntStream.range(0,4).forEach(i->{
            try {
                this.consumer = consumerBuilder.consumerName(String.format("iot-core-consumer-%d", i))
                        .messageListener(this::process)
                        .subscribe();
            } catch (PulsarClientException e) {
                log.error("状态订阅失败：{}", e.getMessage());
            }
        });

        this.producer = this.client.newProducer()
                .producerName("iot-timer-action-producer-" + UUID.randomUUID())
                .topic(QUEUE_TOPIC)
                .create();
    }

    @SneakyThrows
    @EventListener
    public void enqueue(TimerActionSchedulerService.ActionSchedule actionSchedule) {
        TimerAction action = actionSchedule.getTimerAction();
        producer.newMessage().deliverAt(action.getTriggerTime().toEpochMilli() - 200) //降低下延迟
                .key(action.getTargetDeviceId().toString())
                .value(marshaller.stream(action).readAllBytes())
                .send();
    }

    private void process(Consumer<byte[]> consumer, Message<byte[]> msg) {
        try {
            TimerAction timerAction = marshaller.parse(CodedInputStream.newInstance(msg.getData()), null);
            Events.post(timerAction);
            consumer.acknowledge(msg);
        } catch (Exception e) {
            log.warn("timer action exception:",e);
            consumer.negativeAcknowledge(msg);
        }
    }
}
