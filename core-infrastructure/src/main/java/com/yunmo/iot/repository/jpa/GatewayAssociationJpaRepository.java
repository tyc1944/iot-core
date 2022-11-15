package com.yunmo.iot.repository.jpa;

import com.yunmo.iot.domain.core.DeviceRemoveEvent;
import com.yunmo.iot.domain.core.GatewayAssociation;
import com.yunmo.iot.domain.core.repository.GatewayAssociationRepository;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GatewayAssociationJpaRepository extends JpaRepository<GatewayAssociation,GatewayAssociation.ID>, GatewayAssociationRepository {
    Page<GatewayAssociation> findAllByGatewayId(Long id, Pageable paging);

    @EventListener
    default void purge(DeviceRemoveEvent event) {
        this.deleteByDeviceIdOrGatewayId(event.getDevice().getId(), event.getDevice().getId());
    }

    void deleteByDeviceIdOrGatewayId(Long deviceId, Long gatewayId);
}
