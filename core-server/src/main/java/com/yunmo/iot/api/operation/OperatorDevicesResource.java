package com.yunmo.iot.api.operation;

import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.iot.repository.jdbi.OperatorDeviceQueryJDBI;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Api(tags = "运营商设备运行统计")
@RestController
@RequestMapping("/api/operator/products/{productId}")
public class OperatorDevicesResource {

    @Autowired
    Jdbi sqlJDBI;

    @ApiOperation("设备在线状态统计")
    @GetMapping("/connections/stats")
    public StatsView getConnectionStats(@Principal Tenant tenant,
                                   @PathVariable Long productId) {
        return StatsView.statusCount(sqlJDBI.withExtension(OperatorDeviceQueryJDBI.class, (query)->
                query.countByProductAndConnectionStatus(tenant.getDomain(),productId)));
    }
}
