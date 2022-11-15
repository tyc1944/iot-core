package com.yunmo.iot.api.ota;

import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.iot.domain.ota.DeviceOTARecord;
import com.yunmo.iot.repository.jpa.DeviceOTARecordJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;


@Transactional
@RestController
@RequestMapping("/api/devices/{id}/ota")
public class DeviceOTARecordResource {

    @Autowired
    DeviceOTARecordJpaRepository deviceOTARecordRepository;


    @GetMapping("/records")
    public Page<DeviceOTARecord> records(@Principal Tenant tenant, @PathVariable long id, Pageable paging) {
        return deviceOTARecordRepository.findByDeviceId(id, paging);
    }
}