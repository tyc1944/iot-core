package com.yunmo.iot.domain.core.service;

import com.google.common.collect.Lists;
import com.yunmo.iot.domain.core.Device;
import com.yunmo.iot.domain.core.GatewayAssociation;
import com.yunmo.iot.domain.core.repository.DeviceRepository;
import com.yunmo.iot.domain.core.repository.GatewayAssociationRepository;
import com.yunmo.iot.pipe.core.DeviceOfflineEvent;
import com.yunmo.iot.pipe.core.DeviceOnlineEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public class DeviceStatusService {
    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    GatewayAssociationRepository gatewayAssociationRepository;

    @EventListener
    @Transactional
    public void onOnlineEvent(DeviceOnlineEvent event) {
        online(event.getDeviceId(), event.getTs());
    }

    @EventListener
    @Transactional
    public void onOfflineEvent(DeviceOfflineEvent event) {
        offline(event.getDeviceId(), event.getTs());
    }


    @Transactional
    public void online(long deviceId, Instant time) {
        List<Long> relatedIds = getAllRelatedDeviceIds(deviceId);
        deviceRepository.online(relatedIds, time);
    }

    @Transactional
    public void offline(long deviceId, Instant time) {
        List<Long> relatedIds = getAllRelatedDeviceIds(deviceId);
        deviceRepository.offline(relatedIds, time);
    }

    private List<Long> getAllRelatedDeviceIds(Long deviceId) {
        Device device = deviceRepository.findById(deviceId).get();
        List<Long> ids = Lists.newLinkedList();
        ids.add(device.getId());
        if(Boolean.TRUE.equals(device.getGateway())) {
            gatewayAssociationRepository.findAllByGatewayId(deviceId).stream()
                    .map(GatewayAssociation::getDeviceId)
                    .collect(Collectors.toCollection(()->ids));
        }

        return ids;
    }
}
