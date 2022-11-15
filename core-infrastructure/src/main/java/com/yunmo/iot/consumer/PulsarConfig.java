package com.yunmo.iot.consumer;

import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PulsarConfig {
    @Bean
    PulsarClient pulsarClient() throws PulsarClientException {
        int cores = Runtime.getRuntime().availableProcessors();
        return  PulsarClient.builder()
                .serviceUrl("pulsar://core-pulsar-proxy:6650")
                .ioThreads(Math.max(1,cores/4))
                .listenerThreads(cores)
                .build();
    }
}
