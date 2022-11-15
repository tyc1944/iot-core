package com.yunmo.iot.api.analysis;


import com.google.common.base.Preconditions;
import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.iot.domain.analysis.AnalysisTable;
import com.yunmo.iot.domain.analysis.repository.AnalysisTableNumberQuery;
import com.yunmo.iot.domain.analysis.value.AggregateNumber;
import com.yunmo.iot.domain.analysis.value.HourlyNumber;
import com.yunmo.iot.domain.core.Project;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@Api(tags="指定项目下所有设备的数值统计")
@RestController
@RequestMapping("/api/projects/{id}/analysis/{analysisId}")
public class ProjectNumberAnalysisResource {

    @Autowired
    AnalysisTableNumberQuery analysisTableNumberQuery;

    @GetMapping("/hourly")
    public List<HourlyNumber> getHourly(@Principal Tenant tenant, @PathVariable("id") Project project,
                                        @PathVariable("analysisId") AnalysisTable table, @RequestParam String col,
                                        @RequestParam(required = false) int[] index,
                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate from,
                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        Preconditions.checkArgument(project.getTenantId().equals(tenant.getDomain()));
        return analysisTableNumberQuery.selectHourly(new long[]{project.getId()}, new long[]{}, table, col, index, from, to);
    }

    @GetMapping("/daily")
    public List<AggregateNumber> getDaily(@Principal Tenant tenant, @PathVariable("id") Project project,
                                          @PathVariable("analysisId") AnalysisTable table, @RequestParam String col,
                                          @RequestParam(required = false) int[] index,
                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate from,
                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        Preconditions.checkArgument(project.getTenantId().equals(tenant.getDomain()));
        return analysisTableNumberQuery.selectDaily(new long[]{project.getId()}, new long[]{}, table, col, index, from, to);
    }


    @GetMapping("/weekly")
    public List<AggregateNumber> getWeekly(@Principal Tenant tenant, @PathVariable("id") Project project,
                                           @PathVariable("analysisId") AnalysisTable table, @RequestParam String col,
                                           @RequestParam(required = false) int[] index,
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate from,
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        Preconditions.checkArgument(project.getTenantId().equals(tenant.getDomain()));
        return analysisTableNumberQuery.selectWeekly(new long[]{project.getId()}, new long[]{}, table, col, index, from, to);
    }


    @GetMapping("/monthly")
    public List<AggregateNumber> getMonthly(@Principal Tenant tenant, @PathVariable("id") Project project,
                                            @PathVariable("analysisId") AnalysisTable table, @RequestParam String col,
                                            @RequestParam(required = false) int[] index,
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate from,
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        Preconditions.checkArgument(project.getTenantId().equals(tenant.getDomain()));
        return analysisTableNumberQuery.selectMonthly(new long[]{project.getId()}, new long[]{}, table, col, index, from, to);
    }


    @GetMapping("/quarterly")
    public List<AggregateNumber> getQuarterly(@Principal Tenant tenant, @PathVariable("id") Project project,
                                              @PathVariable("analysisId") AnalysisTable table, @RequestParam String col,
                                              @RequestParam(required = false) int[] index,
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate from,
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        Preconditions.checkArgument(project.getTenantId().equals(tenant.getDomain()));
        return analysisTableNumberQuery.selectQuarterly(new long[]{project.getId()}, new long[]{}, table, col, index, from, to);
    }


    @GetMapping("/yearly")
    public List<AggregateNumber> getYearly(@Principal Tenant tenant, @PathVariable("id") Project project,
                                           @PathVariable("analysisId") AnalysisTable table, @RequestParam String col,
                                           @RequestParam(required = false) int[] index,
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate from,
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        Preconditions.checkArgument(project.getTenantId().equals(tenant.getDomain()));
        return analysisTableNumberQuery.selectYearly(new long[]{project.getId()}, new long[]{}, table, col, index, from, to);
    }



}
