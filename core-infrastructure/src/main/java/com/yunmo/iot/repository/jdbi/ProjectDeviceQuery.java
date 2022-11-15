package com.yunmo.iot.repository.jdbi;

import com.yunmo.generator.annotation.JDBI;
import com.yunmo.iot.repository.jdbi.values.ConnectionStatusCount;
import com.yunmo.iot.repository.jdbi.values.DeviceSelectView;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.springframework.data.domain.Pageable;

import java.util.List;

@JDBI
public interface ProjectDeviceQuery {
    @RegisterBeanMapper(ConnectionStatusCount.class)
    List<ConnectionStatusCount> countByProductAndConnectionStatus(Long projectId, Long productId);
}
