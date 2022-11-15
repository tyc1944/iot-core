package com.yunmo.iot.api.operation;

import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.domain.common.Asserts;
import com.yunmo.iot.domain.core.Product;
import com.yunmo.iot.domain.core.Project;
import com.yunmo.iot.repository.jdbi.AlertsCountStatsQueryJDBI;
import com.yunmo.iot.repository.jdbi.ConnectionEventJDBIRepository;
import com.yunmo.iot.repository.jdbi.ProjectDeviceQueryJDBI;
import com.yunmo.iot.repository.jdbi.values.IntervalUnit;
import com.yunmo.iot.repository.jdbi.values.OnlineOfflineStats;
import com.yunmo.iot.repository.jdbi.values.ProjectOverviewStats;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Api(tags = "项目相关统计")
@RestController
@RequestMapping("/api/projects/{projectId}")
public class ProjectStatsResource {

    @Autowired
    ConnectionEventJDBIRepository connectionEventJDBIRepository;

    @Autowired
    Jdbi sqlJDBI;

    @GetMapping("/connections/stats")
    public List<OnlineOfflineStats> getConnectionStats(@Principal Tenant tenant,
                                                       @PathVariable("projectId") Project project,
                                                       @RequestParam IntervalUnit interval,
                                                       @ApiParam("例如:2021-08-30T16:00:00.000Z")
                                                       @RequestParam Instant from,
                                                       @ApiParam("例如:2021-08-30T16:00:00.000Z")
                                                       @RequestParam(required = false) Instant to) {
        Asserts.found(project, p -> project.getTenantId().equals(tenant.getDomain()));
        return connectionEventJDBIRepository.periodCounts(project.getId(), interval, from,
                Optional.ofNullable(to).orElse(Instant.now()));
    }

    @GetMapping("/overview")
    public ProjectOverviewStats overviewStats(@Principal Tenant tenant,
                                              @PathVariable("projectId") Project project,
                                              @RequestParam int zone) {
        Asserts.found(project, p -> project.getTenantId().equals(tenant.getDomain()));
        return connectionEventJDBIRepository.projectOverviewStats(project.getId(), ZoneOffset.ofHours(zone));
    }


    @ApiOperation("项目下设备报警分类统计")
    @GetMapping("/alerts/counts")
    public StatsView alertCounts(@Principal Tenant tenant,
                                 @PathVariable("projectId") Project project,
                                 @ApiParam("例如:2021-08-30T16:00:00.000Z")
                                             @RequestParam Instant from,
                                 @ApiParam("例如:2021-08-30T16:00:00.000Z")
                                             @RequestParam(required = false) Instant to) {
        Asserts.found(project, p -> project.getTenantId().equals(tenant.getDomain()));
        return StatsView.alertCount(sqlJDBI.withExtension(AlertsCountStatsQueryJDBI.class, (query) ->
                query.statsOfProject(project.getId(), from, to)));
    }

    @ApiOperation("项目下设备分产品数量统计")
    @GetMapping("/devices/counts")
    public StatsView productCountStats(@Principal Tenant tenant,
                                       @PathVariable("projectId") Project project,
                                       @RequestParam long productId) {
        Asserts.found(project, p -> project.getTenantId().equals(tenant.getDomain()));
        return StatsView.statusCount(sqlJDBI.withExtension(ProjectDeviceQueryJDBI.class, (query) ->
                query.countByProductAndConnectionStatus(project.getId(), productId)));
    }
}
