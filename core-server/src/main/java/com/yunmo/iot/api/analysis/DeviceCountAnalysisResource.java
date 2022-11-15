package com.yunmo.iot.api.analysis;


import com.google.common.base.Preconditions;
import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.iot.api.core.DeviceResourceBase;
import com.yunmo.iot.domain.analysis.AnalysisTable;
import com.yunmo.iot.domain.analysis.repository.AnalysisTableCountQuery;
import com.yunmo.iot.domain.analysis.value.AggregateCount;
import com.yunmo.iot.domain.analysis.value.HourlyCount;
import com.yunmo.iot.domain.core.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/devices/{id}/analysis/{analysisId}")
public class DeviceCountAnalysisResource extends DeviceResourceBase {

    @Autowired
    AnalysisTableCountQuery analysisTableCountQuery;

    @GetMapping("/hourly/count")
    public List<HourlyCount> getHourly(@Principal Tenant tenant, @PathVariable("id") Device device,
                                       @PathVariable("analysisId") AnalysisTable table, @RequestParam String col,
                                       @RequestParam(required = false) int[] index,
                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate from,
                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        check(tenant, device, table);
        return analysisTableCountQuery.selectHourly(new long[]{device.getProjectId()}, new long[]{device.getId()},
                table, col, index, from, to);
    }

    @GetMapping("/daily/count")
    public List<AggregateCount> getDaily(@Principal Tenant tenant, @PathVariable("id") Device device,
                                         @PathVariable("analysisId") AnalysisTable table, @RequestParam String col,
                                         @RequestParam(required = false) int[] index,
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate from,
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        check(tenant, device,table);
        return analysisTableCountQuery.selectDaily(new long[]{device.getProjectId()}, new long[]{device.getId()},
                table, col, index, from, to);
    }


    @GetMapping("/weekly/count")
    public List<AggregateCount> getWeekly(@Principal Tenant tenant, @PathVariable("id") Device device,
                                          @PathVariable("analysisId") AnalysisTable table, @RequestParam String col,
                                          @RequestParam(required = false) int[] index,
                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate from,
                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        check(tenant, device,table);
        return analysisTableCountQuery.selectWeekly(new long[]{device.getProjectId()}, new long[]{device.getId()},
                table, col, index, from, to);
    }


    @GetMapping("/monthly/count")
    public List<AggregateCount> getMonthly(@Principal Tenant tenant, @PathVariable("id") Device device,
                                           @PathVariable("analysisId") AnalysisTable table, @RequestParam String col,
                                           @RequestParam(required = false) int[] index,
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate from,
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        check(tenant, device,table);
        return analysisTableCountQuery.selectMonthly(new long[]{device.getProjectId()}, new long[]{device.getId()},
                table, col, index, from, to);
    }


    @GetMapping("/quarterly/count")
    public List<AggregateCount> getQuarterly(@Principal Tenant tenant, @PathVariable("id") Device device,
                                             @PathVariable("analysisId") AnalysisTable table, @RequestParam String col,
                                             @RequestParam(required = false) int[] index,
                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate from,
                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        check(tenant, device,table);
        return analysisTableCountQuery.selectQuarterly(new long[]{device.getProjectId()},new long[]{device.getId()},
                table, col, index, from, to);
    }


    @GetMapping("/yearly/count")
    public List<AggregateCount> getYearly(@Principal Tenant tenant, @PathVariable("id") Device device,
                                          @PathVariable("analysisId") AnalysisTable table, @RequestParam String col,
                                          @RequestParam(required = false) int[] index,
                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate from,
                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        check(tenant, device,table);
        return analysisTableCountQuery.selectYearly(new long[]{device.getProjectId()}, new long[]{device.getId()},
                table, col, index, from, to);
    }

    public void check(Tenant tenant, Device device, AnalysisTable table) {
        checkWithProject(device, tenant);
        Preconditions.checkState(device.getProductId().equals(table.getProductId()), "设备与分析产品不匹配！");
    }
}
