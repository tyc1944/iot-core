package com.yunmo.iot.domain.rule.service;

import com.yunmo.iot.domain.core.Device;
import com.yunmo.iot.domain.core.DeviceConnectionStatus;
import com.yunmo.iot.domain.core.DeviceOperationStatus;
import com.yunmo.iot.domain.core.Project;
import com.yunmo.iot.domain.core.repository.DeviceRepository;
import com.yunmo.iot.domain.core.repository.ProjectRepository;
import com.yunmo.iot.domain.rule.AlertPrincipal;
import com.yunmo.iot.domain.rule.AlertStatus;
import com.yunmo.iot.domain.rule.AlertType;
import com.yunmo.iot.domain.rule.RuleAlert;
import com.yunmo.iot.domain.rule.repository.RuleAlertRepository;
import com.yunmo.iot.pipe.core.DeviceOfflineEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlertAudienceService {
    @Autowired
    RuleAlertRepository ruleAlertRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    DeviceRepository deviceRepository;

    @Transactional
    public void dispatch(RuleAlert alert) {
        if(!AlertPrincipal.THING.equals(alert.getPrincipal())) {
            Device device = deviceRepository.findById(alert.getDeviceId()).get();

            Project project = projectRepository.findById(device.getProjectId()).get();
            alert.setProjectId(project.getId());
            alert.setAudienceTenantId(project.getTenantId());
            ruleAlertRepository.save(alert);

            device.setOperationStatus(DeviceOperationStatus.MALFUNCTION);
            deviceRepository.save(device);
        }
    }

    @EventListener
    public void offline(DeviceOfflineEvent event) {
        Device device = deviceRepository.findById(event.getDeviceId()).get();

        RuleAlert alert = RuleAlert.builder().principal(AlertPrincipal.DEVICE)
                .alertStatus(AlertStatus.TRIGGER)
                .productId(device.getProductId())
                .alertTime(event.getTs())
                .alertType(AlertType.OFFLINE.name())
                .notification("设备离线")
                .deviceId(event.getDeviceId())
                .projectId(device.getProjectId())
                .build();
        ruleAlertRepository.save(alert);
    }
}
