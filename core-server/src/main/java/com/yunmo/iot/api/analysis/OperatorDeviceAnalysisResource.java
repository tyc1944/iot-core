package com.yunmo.iot.api.analysis;


import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.iot.domain.analysis.AnalysisTable;
import com.yunmo.iot.domain.analysis.repository.AnalysisTableNumberQuery;
import com.yunmo.iot.domain.analysis.value.AggregateNumber;
import com.yunmo.iot.domain.analysis.value.HourlyNumber;
import com.yunmo.iot.domain.core.Project;
import com.yunmo.iot.repository.jdbi.OperatorDeviceQueryJDBI;
import com.yunmo.iot.repository.jpa.ProjectJpaRepository;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@Api(tags="运营商下所有设备的数值统计")
@RestController
@RequestMapping("/api/operator/analysis/{analysisId}")
public class OperatorDeviceAnalysisResource {

    @Autowired
    AnalysisTableNumberQuery analysisTableNumberQuery;

    @Autowired
    ProjectJpaRepository projectRepository;

    @GetMapping("/hourly")
    public List<HourlyNumber> getHourly(@Principal Tenant tenant,
                                        @PathVariable("analysisId") AnalysisTable table, @RequestParam String col,
                                        @RequestParam(required = false) int[] index,
                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate from,
                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        long[] projectIds = projectRepository.findByOperatorId(tenant.getDomain())
                .stream().mapToLong(Project::getId).toArray();
        return analysisTableNumberQuery.selectHourly(projectIds, new long[]{}, table, col, index, from, to);
    }

    @GetMapping("/daily")
    public List<AggregateNumber> getDaily(@Principal Tenant tenant,
                                          @PathVariable("analysisId") AnalysisTable table, @RequestParam String col,
                                          @RequestParam(required = false) int[] index,
                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate from,
                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        long[] projectIds = projectRepository.findByOperatorId(tenant.getDomain())
                .stream().mapToLong(Project::getId).toArray();
        return analysisTableNumberQuery.selectDaily(projectIds, new long[]{}, table, col, index, from, to);
    }


    @GetMapping("/weekly")
    public List<AggregateNumber> getWeekly(@Principal Tenant tenant,
                                           @PathVariable("analysisId") AnalysisTable table, @RequestParam String col,
                                           @RequestParam(required = false) int[] index,
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate from,
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        long[] projectIds = projectRepository.findByOperatorId(tenant.getDomain())
                .stream().mapToLong(Project::getId).toArray();
        return analysisTableNumberQuery.selectWeekly(projectIds, new long[]{}, table, col, index, from, to);
    }


    @GetMapping("/monthly")
    public List<AggregateNumber> getMonthly(@Principal Tenant tenant,
                                            @PathVariable("analysisId") AnalysisTable table, @RequestParam String col,
                                            @RequestParam(required = false) int[] index,
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate from,
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        long[] projectIds = projectRepository.findByOperatorId(tenant.getDomain())
                .stream().mapToLong(Project::getId).toArray();
        return analysisTableNumberQuery.selectMonthly(projectIds, new long[]{}, table, col, index, from, to);
    }


    @GetMapping("/quarterly")
    public List<AggregateNumber> getQuarterly(@Principal Tenant tenant,
                                              @PathVariable("analysisId") AnalysisTable table, @RequestParam String col,
                                              @RequestParam(required = false) int[] index,
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate from,
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        long[] projectIds = projectRepository.findByOperatorId(tenant.getDomain())
                .stream().mapToLong(Project::getId).toArray();
        return analysisTableNumberQuery.selectQuarterly(projectIds, new long[]{}, table, col, index, from, to);
    }


    @GetMapping("/yearly")
    public List<AggregateNumber> getYearly(@Principal Tenant tenant,
                                           @PathVariable("analysisId") AnalysisTable table, @RequestParam String col,
                                           @RequestParam(required = false) int[] index,
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate from,
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        long[] projectIds = projectRepository.findByOperatorId(tenant.getDomain())
                .stream().mapToLong(Project::getId).toArray();
        return analysisTableNumberQuery.selectYearly(projectIds, new long[]{}, table, col, index, from, to);
    }



}
