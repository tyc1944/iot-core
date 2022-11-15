package com.yunmo.iot.domain.core.service;

import com.yunmo.iot.domain.core.DeviceOwnerShip;
import com.yunmo.iot.domain.core.repository.DeviceOwnerShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DeviceOwnerShipService {

    @Autowired
    DeviceOwnerShipRepository ownerShipRepository;

    public boolean isOwner(Long tenantId, Long deviceId) {
        return ownerShipRepository.findById(new DeviceOwnerShip.ID(tenantId, deviceId)).isPresent();
    }

    public void own(Long tenantId, Long deviceId) {
        ownerShipRepository.save(new DeviceOwnerShip().setDeviceId(deviceId).setOwnerTenantId(tenantId));
    }
}
