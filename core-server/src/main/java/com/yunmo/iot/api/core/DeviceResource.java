package com.yunmo.iot.api.core;

import com.yunmo.domain.common.Asserts;
import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Problems;
import com.yunmo.domain.common.Tenant;
import com.yunmo.iot.domain.core.*;
import com.yunmo.iot.domain.core.repository.GatewayAssociationRepository;
import com.yunmo.iot.domain.core.value.DeviceValue;
import com.yunmo.iot.domain.event.DeviceChangedType;
import com.yunmo.iot.repository.jpa.DeviceJpaRepository;
import com.yunmo.iot.repository.jpa.HubJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

@Transactional
@RestController
@RequestMapping("/api/devices")
@Validated
public class DeviceResource extends DeviceResourceBase {


    @Autowired
    DeviceJpaRepository deviceRepository;

    @Autowired
    HubJpaRepository hubRepository;

    @Autowired
    GatewayAssociationRepository associationRepository;

    @PostMapping("/provision")
    public DeviceProvision provision(Authentication authentication,@NotNull @RequestParam String localId) {
        Long hubId = Long.valueOf(authentication.getCredentials().toString().substring(3));

        DeviceHub hub = hubRepository.getOne(hubId);

        Device device = deviceRepository.findByProductIdAndLocalId(hub.getProductId(), localId);
        if (device == null) {
            device = new Device()
                    .setProductId(hub.getProductId())
                    .setLocalId(localId)
                    .setHubId(hubId);
            deviceRepository.save(device);
        }

        DeviceAuthorization deviceAuthorization = new DeviceAuthorization()
                .setClientId(String.format("hubs/%s/devices/%s", device.getHubId(), device.getId()))
                .setUsername(device.getId().toString())
                .setPassword(CredentialsUtil.createJwt(hub, device))
        ;
       return DeviceProvision.builder()
                .device(device)
                .deviceAuthorization(deviceAuthorization)
                .build();

    }

    @PostMapping
    public Device create(@Principal Tenant tenant, @RequestBody DeviceValue value) {
        Device device = value.create();

        checkWithProject(device, tenant);
        checkWithProduct(device);

        Problems.ensure(value.getHubId() != null, "创建设备接入点不能为空");

        device = deviceRepository.save(device);

        DeviceChangedType deviceChangedType = DeviceChangedType.ADD;
        if (device.getProjectId() != null) {
            deviceChangedType = DeviceChangedType.INSTALL;
        }
        sendMessage(tenant, device, deviceChangedType);
        return device;
    }



    @GetMapping("/{id}")
    public Device getById(@Principal Tenant tenant, @PathVariable("id") Device device) {
        checkWithProject(device, tenant);
        return device;
    }

    @PutMapping("/{id}")
    public void update(@Principal Tenant tenant, @PathVariable("id") Device device, @RequestBody DeviceValue value) {
        checkWithProject(device, tenant);
        checkWithProduct(device);

        if(Boolean.TRUE.equals(value.getGateway())) {
            Problems.ensure(associationRepository.findByDeviceId(device.getId()).isEmpty(), "已绑定子设备不可修改为网关");
        }
        deviceRepository.save(value.assignTo(device));
    }

    @PatchMapping("/{id}")
    public void patch(@Principal Tenant tenant, @PathVariable("id") Device device, @RequestBody DeviceValue value) {
        checkWithProject(device, tenant);
        checkWithProduct(device);

        if(Boolean.TRUE.equals(value.getGateway())) {
            Problems.ensure(associationRepository.findByDeviceId(device.getId()).isEmpty(), "已绑定子设备不可修改为网关");
        }
        deviceRepository.save(value.patchTo(device));
    }

    @DeleteMapping("/{id}")
    public void deleteByID(@Principal Tenant tenant, @PathVariable("id") Device device) {
        checkWithProject(device, tenant);

        deviceRepository.delete(device);
    }

}
