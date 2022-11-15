package com.yunmo.iot.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunmo.domain.common.Events;
import com.yunmo.domain.common.TopicMessage;
import com.yunmo.iot.domain.core.DeviceCommandRecord;
import com.yunmo.iot.domain.core.DeviceConfigEvent;
import com.yunmo.iot.domain.core.StringFormat;
import com.yunmo.iot.pipe.core.Topics;
import lombok.SneakyThrows;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class DeviceNotificationSender {

    ObjectMapper mapper = new ObjectMapper();

    @SneakyThrows
    @EventListener
    public void sendConfig(DeviceConfigEvent event) {
        Topics.Topic topic = Topics.deviceConfig(event.getDevice().getId());
        byte[] content = mapper.writeValueAsString(event.getConfig()).getBytes(StandardCharsets.UTF_8);
        Events.post(new TopicMessage()
                .setTopic(topic.toString())
                .setRetain(true)
                .setContent(content));
    }

    @SneakyThrows
    @EventListener
    public void sendCommand(DeviceCommandRecord record) {
        Topics.Topic topic = Topics.deviceCommand(record.getDeviceId(), record.getChannel());
        byte[] content = null;
        if (record.getContent() != null) {
            if (record.getFormat().equals(StringFormat.HEX)) {
                content = Hex.decode(record.getContent().replaceAll("\\s",""));
            } else {
                content = record.getContent().getBytes(StandardCharsets.UTF_8);
            }
        }
        Events.post(new TopicMessage().setTopic(topic.toString()).setContent(content));
    }
}
