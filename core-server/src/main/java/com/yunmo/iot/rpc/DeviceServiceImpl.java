package com.yunmo.iot.rpc;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunmo.domain.common.Events;
import com.yunmo.iot.api.core.DeviceService;
import com.yunmo.iot.domain.core.*;
import com.yunmo.iot.domain.core.repository.DeviceHubRepository;
import com.yunmo.iot.domain.core.repository.DeviceRepository;
import com.yunmo.iot.domain.core.service.DeviceStatusService;
import com.yunmo.iot.domain.core.service.GatewayService;
import com.yunmo.iot.domain.core.value.DeviceValue;
import com.yunmo.iot.pipe.core.DeviceOfflineEvent;
import com.yunmo.iot.pipe.core.DeviceOnlineEvent;
import com.yunmo.iot.repository.jpa.GatewayAssociationJpaRepository;
import com.yunmo.iot.repository.jpa.HubJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceServiceImpl implements DeviceService {
    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    DeviceHubRepository deviceHubRepository;

    @Autowired
    GatewayAssociationJpaRepository gatewayAssociationRepository;

    @Autowired
    GatewayService gatewayService;

    @Autowired
    HubJpaRepository hubRepository;


    @Override
    public Device create(DeviceValue value) {
        Device device = value.create();

        return deviceRepository.save(device);
    }

    @Override
    public Device save(Device device) {
        return deviceRepository.save(device);
    }

    @Override
    public void saveAll(List<Device> devices) {
         deviceRepository.saveAll(devices);
    }

    @Override
    public Device getDeviceById(Long id) {
        return deviceRepository.findById(id).orElse(null);
    }

    @Override
    public void setOnlineStatus(long id, boolean online) {
        Device device = deviceRepository.findById(id).get();
        if(online) {
            Events.post( new DeviceOnlineEvent()
                    .setDeviceId(id)
                    .setHubId(device.getHubId())
                    .setTs(Instant.now()));
        } else {
            Events.post( new DeviceOfflineEvent()
                    .setDeviceId(id)
                    .setHubId(device.getHubId())
                    .setTs(Instant.now()));
        }
    }

    @Override
    public Device getDeviceByLocalId(long productId, String localId) {
        return deviceRepository.findByProductIdAndLocalId(productId, localId);
    }

    @Override
    public Device autoProvisionSubDevice(long gatewayId, String localId) {
        List<Device> devices = gatewayService.getChildrenByLocalId(gatewayId, Lists.newArrayList(localId));
        if(!devices.isEmpty()) {
            return devices.get(0);
        }

        Device gateway = deviceRepository.findById(gatewayId).get();
        DeviceHub hub = deviceHubRepository.findById(gateway.getHubId()).get();
        Preconditions.checkState(ProvisionStrategy.AUTO.equals(hub.getProvisionStrategy()),
                "接入点不允许设备自动注册");

        Device device = new Device();
        device.setLocalId(localId);
        device.setHubId(hub.getId());
        device.setGateway(false);
        device.setProjectId(gateway.getProjectId());
        device.setDeviceStatus(DeviceConnectionStatus.ONLINE);
        deviceRepository.save(device);

        gatewayService.attach(gateway, List.of(device.getId()));
        return device;
    }


    @Override
    public DeviceHub getHubById(Long id) {
        return deviceHubRepository.findById(id).orElse(null);
    }

    @Override
    public List<Device> getGatewayChildren(Long gatewayId) {
        List<GatewayAssociation> children = gatewayAssociationRepository.findAllByGatewayId(gatewayId);
        var ids = children.stream().map(GatewayAssociation::getDeviceId).collect(Collectors.toList());
        return Lists.newArrayList(deviceRepository.findAllById(ids));
    }

    @Override
    public List<Device> getByProjectId(long projectId){
        return deviceRepository.findByProjectId(projectId);
    }


    @Override
    public List<Device> findByConfigContentProperty(String key,String value){
        return deviceRepository.findByConfigContentProperty(key,value);
    }

    @Override
    public List<Device> findByAttributesProperty(String key, String value) {
        return deviceRepository.findByAttributesProperty(key,value);
    }

    @Override
    public List<Device> findAllInIds(List<Long> ids) {
        return deviceRepository.findAllById(ids);
    }

}
