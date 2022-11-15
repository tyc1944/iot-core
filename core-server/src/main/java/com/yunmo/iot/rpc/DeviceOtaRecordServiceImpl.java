package com.yunmo.iot.rpc;

import com.yunmo.iot.api.core.DeviceOtaRecordService;
import com.yunmo.iot.domain.ota.DeviceOTARecord;
import com.yunmo.iot.repository.jpa.DeviceOTARecordJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceOtaRecordServiceImpl implements DeviceOtaRecordService {

    @Autowired
    DeviceOTARecordJpaRepository deviceOTARecordJpaRepository;

    @Override
    public DeviceOTARecord getLastByDeviceId(long deviceId) {
        return deviceOTARecordJpaRepository.findFirstByDeviceIdOrderByCreatedDate(deviceId);
    }
}
