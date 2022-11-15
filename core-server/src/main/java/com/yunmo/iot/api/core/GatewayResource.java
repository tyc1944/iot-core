package com.yunmo.iot.api.core;

import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.boot.web.ApiPageable;
import com.yunmo.domain.common.Asserts;
import com.yunmo.domain.common.Paged;
import com.yunmo.generator.annotation.JDBI;
import com.yunmo.iot.domain.core.Device;
import com.yunmo.iot.domain.core.GatewayAssociation;
import com.yunmo.iot.domain.core.service.GatewayService;
import com.yunmo.iot.domain.core.value.DeviceChildValue;
import com.yunmo.iot.repository.jdbi.GatewayChildrenQuery;
import com.yunmo.iot.repository.jdbi.GatewayChildrenQueryJDBI;
import com.yunmo.iot.repository.jdbi.values.DeviceSelectView;
import com.yunmo.iot.repository.jpa.DeviceJpaRepository;
import com.yunmo.iot.repository.jpa.GatewayAssociationJpaRepository;
import io.swagger.annotations.ApiOperation;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;


@Transactional
@RestController
@RequestMapping("/api/gateways")
public class GatewayResource extends DeviceResourceBase {
    @Autowired
    GatewayService gatewayService;

    @Autowired
    GatewayAssociationJpaRepository associationRepository;

    @Autowired
    DeviceJpaRepository deviceRepository;

    @Autowired
    Jdbi sqlJDBI;

    @PostMapping("/{id}/attach")
    public void attach(@Principal Tenant tenant, @PathVariable("id") Device gateway, @RequestBody List<Long> childDeviceIds) {
        checkWithProject(gateway, tenant);
        gatewayService.attach(gateway, childDeviceIds);
    }

    @PostMapping("/{id}/detach")
    public void detach(@Principal Tenant tenant, @PathVariable("id") Device gateway, @RequestBody List<Long> childDeviceIds) {
        checkWithProject(gateway, tenant);
        gatewayService.detach(gateway, childDeviceIds);
    }


    @ApiPageable
    @GetMapping("/{id}/children")
    public Paged<Device> children(@Principal Tenant tenant, @PathVariable("id") Device gateway, Pageable paging) {
        checkWithProject(gateway, tenant);

        Page<GatewayAssociation> children = associationRepository.findAllByGatewayId(gateway.getId(), paging);
        var ids = children.getContent().stream()
                .map(GatewayAssociation::getDeviceId)
                .collect(Collectors.toList());
        return Paged.of(children, deviceRepository.findAllById(ids));
    }

    @ApiPageable
    @PutMapping("/{id}/children/{childId}")
    public void updateChild(@Principal Tenant tenant, @PathVariable("id") Device gateway,
                            @PathVariable("childId") Device child, @RequestBody DeviceChildValue childValue) {
        checkWithProject(gateway, tenant);
        Asserts.found(associationRepository.findById(new GatewayAssociation.ID(child.getId(), gateway.getId())));
        deviceRepository.save(childValue.assignTo(child));
    }

    @ApiOperation(value = "候选设备列表", notes = "必须是同接入点下的")
    @ApiPageable
    @GetMapping("/{id}/candidates")
    public Page<DeviceSelectView> candidates(@Principal Tenant tenant, @PathVariable("id") Device gateway, Pageable paging) {
        return sqlJDBI.withExtension(GatewayChildrenQueryJDBI.class, dao ->
                dao.deviceNotAssociateGatewayPage(gateway.getProjectId(), paging));
    }
}
