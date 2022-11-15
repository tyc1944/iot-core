package com.yunmo.iot.domain.core.service;

import com.yunmo.iot.domain.core.*;
import com.yunmo.iot.domain.core.repository.DeviceConfigRecordRepository;
import com.yunmo.iot.domain.core.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class ShadowService  {
    @Autowired
    DeviceConfigRecordRepository configRecordRepository;

    @Autowired
    DeviceRepository deviceRepository;

    @EventListener
    public void onConfig(DeviceConfigEvent event) {
        Device device = event.getDevice();
        DeviceConfig config = event.getConfig();
        configRecordRepository.save(new DeviceConfigRecord()
            .setDeviceId(device.getId())
            .setVersion(config.getVersion())
            .setStatus(ConfigResultCode.PENDING)
            .setContent(config.getContent()));
    }

    @Transactional
    @EventListener
    public void onStateReported(DeviceStateReportEvent event) {
        Device device = deviceRepository.findById(event.getDeviceId()).get();
        DeviceState state = event.getState();
        state.setTimestamp(Instant.now());//不需要设备传timestamp
        if(!state.getResult().equals(ConfigResultCode.OVERRIDE)) {
            refreshConfigOpsState(device.getId(), device.getState().getVersion(),
                    state.getVersion(), state.getResult());
        }
        device.setState(state);
        deviceRepository.save(device);
    }

    public void refreshConfigOpsState(long deviceId, long versionFrom, long versionTo, ConfigResultCode result) {
        List<DeviceConfigRecord> operations = configRecordRepository.findByDeviceIdAndVersionGreaterThanAndVersionGreaterThanEqual(
                deviceId, versionFrom, versionTo);
        operations.forEach(o->configRecordRepository.save(o.setStatus(result)));
    }

}
