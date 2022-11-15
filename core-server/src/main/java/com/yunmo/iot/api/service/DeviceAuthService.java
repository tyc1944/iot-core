package com.yunmo.iot.api.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.google.common.base.Preconditions;
import com.yunmo.domain.common.Asserts;
import com.yunmo.iot.domain.core.CredentialsUtil;
import com.yunmo.iot.domain.core.Device;
import com.yunmo.iot.domain.core.DeviceHub;
import com.yunmo.iot.repository.jpa.HubJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/devices/auth")
@PreAuthorize("hasRole('INTERNAL')")
public class DeviceAuthService {
    @Autowired
    HubJpaRepository hubRepository;

    public static final Pattern CLIENT_ID_PATTERN = Pattern.compile("hubs/(\\d+)/devices/(\\d+)(?:/(\\w+))?");


    @GetMapping
    public void auth(@RequestParam String cid, @RequestParam("id")Device device, @RequestParam String token) {
        Asserts.found(device);

        var matcher = CLIENT_ID_PATTERN.matcher(cid);
        matcher.matches();
        Preconditions.checkArgument(device.getHubId().equals(Long.parseLong(matcher.group(1)))
        && device.getId().equals(Long.parseLong(matcher.group(2))), "client id信息不匹配");

        String component = matcher.group(3);
        if(component != null) {
            Preconditions.checkNotNull(device.getComponents(), "设备组件不存在");
            Preconditions.checkArgument(device.getComponents().contains(component), "设备组件不存在");
        }

        DeviceHub hub = hubRepository.findById(device.getHubId()).get();
        try {
            CredentialsUtil.verifyAndDecodeJwt(hub, device, token);
        } catch (JWTVerificationException e){
            throw Problem.valueOf(Status.UNAUTHORIZED, e.getMessage());
        }
    }
}
