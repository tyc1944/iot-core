package com.yunmo.iot.repository.jdbi;

import com.yunmo.generator.annotation.JDBI;
import com.yunmo.iot.repository.jdbi.values.ConnectionStatusCount;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;

import java.util.List;

@JDBI
public interface OperatorDeviceQuery {
    @RegisterBeanMapper(ConnectionStatusCount.class)
    List<ConnectionStatusCount> countByProductAndConnectionStatus(Long operatorId, Long productId);

    @RegisterBeanMapper(ConnectionStatusCount.class)
    List<ConnectionStatusCount> countConnectionStatusByProduct(Long productId);
}
