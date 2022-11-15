package com.yunmo.iot.domain.core.repository;

import com.yunmo.domain.common.EntityRepository;
import com.yunmo.iot.domain.core.GatewayAssociation;

import java.util.List;
import java.util.Optional;

public interface GatewayAssociationRepository extends EntityRepository<GatewayAssociation, GatewayAssociation.ID> {

    List<GatewayAssociation> findAllByGatewayId(Long id);
    List<GatewayAssociation> findByGatewayIdIn(List<Long> ids);

    void deleteByDeviceId(Long id);

    List<GatewayAssociation> findByDeviceIdIn(List<Long> deviceIds);

    Optional<GatewayAssociation> findByDeviceId(Long id);
}
