package com.yunmo.iot.api.core;

import com.google.common.base.Preconditions;
import com.yunmo.domain.common.Tenant;
import com.yunmo.domain.common.Asserts;
import com.yunmo.iot.domain.core.Device;
import com.yunmo.iot.domain.core.service.ProjectDeviceInstallService;
import com.yunmo.iot.domain.event.DeviceChangedEvent;
import com.yunmo.iot.domain.event.DeviceChangedType;
import com.yunmo.iot.producer.DeviceChangedEventPulsarProducer;
import com.yunmo.iot.repository.jpa.DeviceJpaRepository;
import com.yunmo.iot.repository.jpa.ProductJpaRepository;
import com.yunmo.iot.repository.jpa.ProjectJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Optional;

public abstract class DeviceResourceBase {
    @Autowired
    ProjectJpaRepository projectRepository;

    @Autowired
    ProductJpaRepository productJpaRepository;

    @Autowired
    ProjectJpaRepository projectJpaRepository;

    @Autowired
    DeviceChangedEventPulsarProducer deviceChangedEventPulsarProducer;

    @Autowired
    DeviceJpaRepository deviceJpaRepository;

    @Autowired
    ProjectDeviceInstallService projectDeviceInstallService;

    protected void checkWithProject(Device device, Tenant tenant) {
        projectDeviceInstallService.checkWithProject(device, tenant);
    }


    protected void checkWithProduct(Device device) {
        Asserts.found(device);
        if (device.getProductId() != null) {
            Optional<Device> optionalDevice = Optional.ofNullable(deviceJpaRepository.findByProductIdAndLocalId(device.getProductId(), device.getLocalId()));
            Preconditions.checkState(optionalDevice.isEmpty() || optionalDevice.get().getId().equals(device.getId()), "产品下该出厂设备已存在");
        }
    }

    protected void sendMessage(Tenant tenant, Device device, DeviceChangedType deviceChangedType) {
        sendMessage(tenant, device, deviceChangedType, device.getProjectId());
    }

    protected void sendMessage(Tenant tenant, Device device, DeviceChangedType deviceChangedType, Long operateProjectId) {
        Long operatorId = null;
        if (operateProjectId!=null) {
            operatorId = projectJpaRepository.findById(operateProjectId).get().getOperatorId();
        }
        Instant now = Instant.now();
        deviceChangedEventPulsarProducer.send(DeviceChangedEvent.builder()
                .type(deviceChangedType)
                .tenantPersonId(tenant.getId())
                .tenantId(tenant.getDomain())
                .projectId(operateProjectId)
                .recordTime(now)
                .operatorId(operatorId)
                .changedDevice(device)
                .build());
    }
}
