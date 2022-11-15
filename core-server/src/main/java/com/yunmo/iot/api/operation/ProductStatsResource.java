package com.yunmo.iot.api.operation;

import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.iot.repository.jdbi.AlertsCountStatsQueryJDBI;
import com.yunmo.iot.repository.jdbi.OperatorDeviceQueryJDBI;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@Api(tags = "产品相关统计")
@RestController
@RequestMapping("/api/products/{productId}")
public class ProductStatsResource {

    @Autowired
    Jdbi sqlJDBI;

    @ApiOperation("产品下设备报警数量统计")
    @GetMapping("/alerts/stats")
    public StatsView getAlertStats(@Principal Tenant tenant,
                                   @PathVariable Long productId,
                                   @ApiParam("例如:2021-08-30T16:00:00.000Z")
                                 @RequestParam Instant from,
                                   @ApiParam("例如:2021-08-30T16:00:00.000Z")
                                 @RequestParam(required = false) Instant to) {
        return StatsView.alertCount(sqlJDBI.withExtension(AlertsCountStatsQueryJDBI.class, (query)->
                query.statsOfProduct(productId,from,to)));
    }

    @ApiOperation("设备在线状态统计")
    @GetMapping("/connections/stats")
    public StatsView getConnectionStats(@Principal Tenant tenant,
                                        @PathVariable Long productId) {
        return StatsView.statusCount(sqlJDBI.withExtension(OperatorDeviceQueryJDBI.class, (query)->
                query.countConnectionStatusByProduct(productId)));
    }
}
