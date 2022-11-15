package com.yunmo.iot.api.core;

import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.boot.web.ApiPageable;
import com.yunmo.domain.common.Asserts;
import com.yunmo.domain.common.Linq;
import com.yunmo.domain.common.Paged;
import com.yunmo.iot.domain.core.Device;
import com.yunmo.iot.domain.core.Product;
import com.yunmo.iot.domain.core.Project;
import com.yunmo.iot.domain.core.value.DeviceBrief;
import com.yunmo.iot.domain.rule.RuleAlert;
import com.yunmo.iot.domain.rule.value.RuleAlertSearch;
import com.yunmo.iot.repository.jpa.DeviceJpaRepository;
import com.yunmo.iot.repository.jpa.RuleAlertJpaRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/projects/{projectId}/alerts")
public class AlertsResource {
    @Autowired
    RuleAlertJpaRepository ruleAlertRepository;

    @Autowired
    DeviceJpaRepository deviceJpaRepository;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AlertDevice {
        private RuleAlert alert;
        private Optional<DeviceBrief> device; //设备可能已经删除
    }

    @ApiPageable
    @GetMapping
    public Paged<AlertDevice> getProjectList(@Principal Tenant tenant, @PathVariable("projectId") Project project,
                                             RuleAlertSearch search, Pageable paging) {
        Asserts.found(project, p->p.getTenantId().equals(tenant.getDomain()));
        RuleAlert example = search.create();
        //example.setAudienceTenantId(tenant.getId());
        example.setProjectId(project.getId());
        Page<RuleAlert> alertPage = ruleAlertRepository.findAll(Example.of(example), paging);
        var joined = Linq.leftJoinBatch(alertPage.getContent(), deviceJpaRepository::findAllById)
                .on(RuleAlert::getDeviceId, Device::getId,
                        (alert, device) -> new AlertDevice(alert, Optional.ofNullable(device).map(DeviceBrief::from)))
                .toList();
        return Paged.of(alertPage, joined);
    }

    @ApiPageable
    @GetMapping("/devices/{deviceId}")
    public Page<RuleAlert> getDeviceAlertList(@Principal Tenant tenant, @PathVariable("projectId") Project project,
                                      @PathVariable Long deviceId, RuleAlertSearch search, Pageable paging) {
        Asserts.found(project, p->p.getTenantId().equals(tenant.getDomain()));
        RuleAlert example = search.create();
        example.setProjectId(project.getId());
        example.setDeviceId(deviceId);
        return ruleAlertRepository.findAll(Example.of(example), paging);
    }
}
