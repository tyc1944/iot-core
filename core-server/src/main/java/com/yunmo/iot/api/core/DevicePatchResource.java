package com.yunmo.iot.api.core;

import com.yunmo.iot.domain.core.repository.GatewayAssociationRepository;
import com.yunmo.iot.repository.jpa.DeviceJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@Transactional
@RestController
@RequestMapping("/api/devices/batches")
public class DevicePatchResource extends DeviceResourceBase {
    @Autowired
    DeviceJpaRepository deviceRepository;



    @Autowired
    GatewayAssociationRepository associationRepository;


}
