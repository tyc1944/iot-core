package com.yunmo.iot.api.core;

import com.yunmo.boot.web.ApiPageable;
import com.yunmo.domain.common.Events;
import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.iot.domain.core.Device;
import com.yunmo.iot.domain.core.DeviceCommandRecord;
import com.yunmo.iot.domain.core.value.DeviceCommandRecordValue;
import com.yunmo.iot.repository.jpa.DeviceCommandRecordJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;


@Transactional
@RestController
@RequestMapping("/api/devices")
public class DeviceCommandResource extends DeviceResourceBase {

    @Autowired
    DeviceCommandRecordJpaRepository deviceCommandRecordRepository;

    @ApiPageable
    @GetMapping("/{id}/commands")
    public Page<DeviceCommandRecord> getCommands(@Principal Tenant tenant, @PathVariable("id") Device device, Pageable paging) {
        checkWithProject(device, tenant);
        return deviceCommandRecordRepository.findAllByDeviceId(device.getId(), paging);
    }

    @PostMapping("/{id}/commands")
    public void sendCommand(@Principal Tenant tenant, @PathVariable("id") Device device, @RequestBody DeviceCommandRecordValue value) {
        checkWithProject(device, tenant);
        DeviceCommandRecord record = value.create()
                .setProjectId(device.getProjectId())
                .setDeviceId(device.getId());
        deviceCommandRecordRepository.save(record);
        Events.post(record);
    }
}
