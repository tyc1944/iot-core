package com.yunmo.iot.consumer;

import com.google.protobuf.CodedInputStream;
import com.yunmo.iot.domain.rule.RuleAlert;
import com.yunmo.iot.domain.rule.RuleAlertMarshaller;
import com.yunmo.iot.domain.rule.service.AlertAudienceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

@Component
@Slf4j
public class RuleAlertPulsarConsumer {
    @Autowired
    private PulsarClient client;

    ConsumerBuilder<byte[]> consumerBuilder;

    private RuleAlertMarshaller marshaller = new RuleAlertMarshaller();

    @Autowired
    AlertAudienceService alertAudienceService;


    @EventListener
    public void run(ContextRefreshedEvent event) {
        this.consumerBuilder = this.client.newConsumer()
                .topic("iot/rule/alert")
                .subscriptionName("iot-core-rule-alert")
                .deadLetterPolicy(DeadLetterPolicy.builder()
                .maxRedeliverCount(3)
                .build())
                .subscriptionType(SubscriptionType.Key_Shared);
        IntStream.range(0,4).forEach(i->{
            try {
                this.consumerBuilder.consumerName(String.format("iot-core-rule-alert-consumer-%d", i))
                        .messageListener(this::process)
                        .subscribe();
            } catch (PulsarClientException e) {
                log.error("状态订阅失败：{}", e.getMessage());
            }
        });
    }

    @Transactional
    public void process(Consumer<byte[]> consumer, Message<byte[]> msg) {
        try {
            RuleAlert alert = marshaller.parse(CodedInputStream.newInstance(msg.getData()), null);
            alertAudienceService.dispatch(alert);
            consumer.acknowledge(msg);
        } catch (Exception e) {
            consumer.negativeAcknowledge(msg);
        }
    }
}
