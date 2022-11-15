package com.yunmo.iot.domain.core.service;

import com.yunmo.domain.common.Problems;
import com.yunmo.iot.domain.core.Device;
import com.yunmo.iot.domain.core.DeviceConnectionStatus;
import com.yunmo.iot.domain.core.GatewayAssociation;
import com.yunmo.iot.domain.core.repository.DeviceRepository;
import com.yunmo.iot.domain.core.repository.GatewayAssociationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GatewayService {
    @Autowired
    GatewayAssociationRepository associationRepository;

    @Autowired
    DeviceRepository deviceRepository;

    public void attach(Device gateway, List<Long> deviceIdsToAttach) {
        List<String> localIds = deviceRepository.findAllById(deviceIdsToAttach).stream()
                .map(Device::getLocalId).collect(Collectors.toList());
        List<Device> localIdSameChildren = getChildrenByLocalId(gateway.getId(), localIds);
        Problems.ensure(localIdSameChildren.isEmpty(),
                "此网关下已存在本地ID相同的设备:"+localIdSameChildren.stream().map(d->d.getLocalId())
                        .collect(Collectors.joining(",")));

        List<GatewayAssociation> attachedAssociations = associationRepository.findByDeviceIdIn(deviceIdsToAttach)
                .stream().filter(a->!a.getGatewayId().equals(gateway.getId())).collect(Collectors.toList());
        Problems.ensure(attachedAssociations.size() == 0,
                String.format("设备%s已绑定到其他网关",attachedAssociations.stream().map(d->d.getDeviceId().toString())
                        .collect(Collectors.joining(","))));

        List<GatewayAssociation> associations = deviceIdsToAttach.stream().map(childId->new GatewayAssociation()
                .setGatewayId(gateway.getId())
                .setDeviceId(childId))
                .collect(Collectors.toList());
        associationRepository.saveAll(associations);

        deviceRepository.setOnlineStatus(deviceIdsToAttach, gateway.getDeviceStatus());
    }

    public List<Device> getChildrenByLocalId(Long gatewayId, List<String> localIds) {
        List<Long> childrenIds = associationRepository.findAllByGatewayId(gatewayId)
                .stream().map(GatewayAssociation::getDeviceId)
                .collect(Collectors.toList());
        return deviceRepository.findByIdInAndLocalIdIn(childrenIds, localIds);
    }

    public void detach(Device gateway, List<Long> childDeviceIds) {
        List<GatewayAssociation.ID> associationIds = childDeviceIds.stream()
                .map(childId->new GatewayAssociation.ID()
                    .setGatewayId(gateway.getId())
                    .setDeviceId(childId))
                .collect(Collectors.toList());

        List<GatewayAssociation> associations = associationRepository.findAllById(associationIds);

        List<Long>  childDevicesIds = associations.stream().map(GatewayAssociation::getDeviceId).collect(Collectors.toList());

        deviceRepository.setOnlineStatus(childDevicesIds, DeviceConnectionStatus.INACTIVE);
        associationRepository.deleteAll(associations);
    }
}
