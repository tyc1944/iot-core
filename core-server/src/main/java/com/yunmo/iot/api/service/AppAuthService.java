package com.yunmo.iot.api.service;

import com.google.common.base.Preconditions;
import com.yunmo.domain.common.Asserts;
import com.yunmo.iot.domain.app.AppProfile;
import com.yunmo.iot.domain.app.AppStatus;
import com.yunmo.iot.domain.core.Device;
import com.yunmo.iot.domain.core.repository.DeviceRepository;
import com.yunmo.iot.pipe.core.Topics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/apps")
@PreAuthorize("hasRole('INTERNAL')")
public class AppAuthService {

    @Autowired
    DeviceRepository deviceRepository;

    @GetMapping("/auth")
    @PreAuthorize("hasRole('INTERNAL')")
    public void auth(@RequestParam("app") AppProfile app, @RequestParam String token) {
        Asserts.found(app, a -> a.getStatus().equals(AppStatus.NORMAL)
                && a.getToken().equals(token));
    }

    @GetMapping("/acl")
    @PreAuthorize("hasRole('INTERNAL')")
    public void acl(@RequestParam("app") AppProfile app, @RequestParam String topic) {
        Asserts.found(app, a -> a.getStatus().equals(AppStatus.NORMAL));
        Topics.Topic topicValue = Topics.parser(topic);
        if(topicValue.getPrefix().equals(Topics.APP_PREFIX)) {
            Preconditions.checkArgument(topicValue.getId().equals(app.getAppKey()));
            return;
        }

        Device device = deviceRepository.findById(Long.parseLong(topicValue.getId())).get();
        Preconditions.checkState(app.inApprovedDevices(device.getId()) || app.inApprovedProjects(device.getProjectId()));
    }
}
