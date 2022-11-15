package com.yunmo.iot.producer;

import com.yunmo.iot.domain.event.DeviceChangedEvent;
import com.yunmo.iot.domain.event.DeviceChangedEventMarshaller;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/** @author lh */
@Component
@Slf4j
public class DeviceChangedEventPulsarProducer {

  private final DeviceChangedEventMarshaller marshaller = new DeviceChangedEventMarshaller();

  @Autowired private PulsarClient client;

  private Producer<byte[]> producer;

  @SneakyThrows
  @EventListener
  public void initProducer(ContextRefreshedEvent event) {
    producer =
        client
            .newProducer()
            .sendTimeout(5, TimeUnit.SECONDS)
            .topic(DeviceChangedEvent.TOPIC)
            .enableBatching(false)
            .create();
  }

  @SneakyThrows
  public void send(DeviceChangedEvent event) {
    producer.send(marshaller.stream(event).readAllBytes());
  }
}
