package com.yunmo.iot.api.core;

import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.boot.web.ApiPageable;
import com.yunmo.domain.common.Asserts;
import com.yunmo.iot.domain.core.Device;
import com.yunmo.iot.domain.core.DeviceConfigRecord;
import com.yunmo.iot.repository.jpa.DeviceConfigRecordJpaRepository;
import com.yunmo.iot.repository.jpa.DeviceJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.Map;


@Transactional
@RestController
@RequestMapping("/api/devices")
public class DeviceConfigResource extends DeviceResourceBase {

    @Autowired
    DeviceJpaRepository deviceRepository;

    @Autowired
    DeviceConfigRecordJpaRepository recordRepository;


    @PostMapping("/{id}/config")
    public Device config(@Principal Tenant tenant, @PathVariable("id") Device device, @RequestBody Map<String,Object> config) {
        checkWithProject(device, tenant);
        device.updateConfig(config);
        return deviceRepository.save(device);
    }

    @GetMapping("/{id}/config/current")
    public Map<String,Object> getCurrentConfig(@Principal Tenant tenant, @PathVariable("id") Device device) {
        checkWithProject(device, tenant);
        return device.getConfig().getVersion() > device.getState().getVersion()?
                device.getConfig().getContent(): device.getState().getContent();
    }

    @ApiPageable
    @GetMapping("/{id}/config/histories")
    public Page<DeviceConfigRecord> getConfigHistory(@PathVariable("id") Device device, Pageable paging) {
        Asserts.found(device);
        return recordRepository.findAllByDeviceId(device.getId(), paging);
    }
}
