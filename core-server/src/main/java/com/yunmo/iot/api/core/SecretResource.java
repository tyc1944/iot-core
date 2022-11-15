package com.yunmo.iot.api.core;

import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.iot.domain.core.Credentials;
import com.yunmo.iot.domain.core.CredentialsUtil;
import com.yunmo.iot.domain.core.Device;
import com.yunmo.iot.domain.core.DeviceHub;
import com.yunmo.iot.repository.jpa.HubJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/secret/")
public class SecretResource extends DeviceResourceBase {

    @Autowired
    HubJpaRepository hubRepository;


    @PostMapping("/credential")
    public Credentials newCredential() {
        return CredentialsUtil.createCredentials();
    }

    @GetMapping("/devices/{id}/token")
    public String token(@Principal Tenant tenant, @PathVariable("id") Device device) {
        checkWithProject(device, tenant);
        DeviceHub hub = hubRepository.getOne(device.getHubId());
        return CredentialsUtil.createJwt(hub, device);
    }
}
