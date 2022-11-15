package com.yunmo.iot.repository.jdbi;

import com.yunmo.generator.annotation.JDBI;
import com.yunmo.iot.domain.core.Device;
import com.yunmo.iot.repository.jdbi.values.DeviceSelectView;
import org.jdbi.v3.core.enums.EnumByName;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.springframework.data.domain.Pageable;

import java.util.List;

@JDBI
public interface GatewayChildrenQuery {
    @RegisterBeanMapper(DeviceSelectView.class)
    List<DeviceSelectView> deviceNotAssociateGateway(Long projectId, Pageable paging);
}
