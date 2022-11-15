package com.yunmo.iot.api.analysis;


import com.google.common.base.Preconditions;
import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.iot.api.core.DeviceResourceBase;
import com.yunmo.iot.domain.analysis.AnalysisTable;
import com.yunmo.iot.domain.analysis.repository.AnalysisTableNumberQuery;
import com.yunmo.iot.domain.analysis.value.AggregateNumber;
import com.yunmo.iot.domain.analysis.value.HourlyNumber;
import com.yunmo.iot.domain.core.Device;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@Api("指定设备的数值统计")
@RestController
@RequestMapping("/api/devices/{id}/analysis/{analysisId}")
public class DeviceNumberAnalysisResource extends DeviceResourceBase {

    @Autowired
    AnalysisTableNumberQuery analysisTableNumberQuery;

    @GetMapping("/hourly")
    public List<HourlyNumber> getHourly(@Principal Tenant tenant, @PathVariable("id") Device device,
                                        @PathVariable("analysisId") AnalysisTable table, @RequestParam String col,
                                        @RequestParam(required = false) int[] index,
                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate from,
                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        check(tenant, device,table);
        return analysisTableNumberQuery.selectHourly(new long[]{device.getProjectId()}, new long[]{device.getId()},
                table, col, index, from, to);
    }

    @GetMapping("/daily")
    public List<AggregateNumber> getDaily(@Principal Tenant tenant, @PathVariable("id") Device device,
                                          @PathVariable("analysisId") AnalysisTable table, @RequestParam String col,
                                          @RequestParam(required = false) int[] index,
                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate from,
                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        check(tenant, device,table);
        return analysisTableNumberQuery.selectDaily(new long[]{device.getProjectId()}, new long[]{device.getId()},
                table, col, index, from, to);
    }


    @GetMapping("/weekly")
    public List<AggregateNumber> getWeekly(@Principal Tenant tenant, @PathVariable("id") Device device,
                                           @PathVariable("analysisId") AnalysisTable table, @RequestParam String col,
                                           @RequestParam(required = false) int[] index,
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate from,
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        check(tenant, device,table);
        return analysisTableNumberQuery.selectWeekly(new long[]{device.getProjectId()}, new long[]{device.getId()},
                table, col, index, from, to);
    }


    @GetMapping("/monthly")
    public List<AggregateNumber> getMonthly(@Principal Tenant tenant, @PathVariable("id") Device device,
                                            @PathVariable("analysisId") AnalysisTable table, @RequestParam String col,
                                            @RequestParam(required = false) int[] index,
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate from,
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        check(tenant, device,table);
        return analysisTableNumberQuery.selectMonthly(new long[]{device.getProjectId()}, new long[]{device.getId()},
                table, col, index, from, to);
    }


    @GetMapping("/quarterly")
    public List<AggregateNumber> getQuarterly(@Principal Tenant tenant, @PathVariable("id") Device device,
                                              @PathVariable("analysisId") AnalysisTable table, @RequestParam String col,
                                              @RequestParam(required = false) int[] index,
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate from,
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        check(tenant, device,table);
        return analysisTableNumberQuery.selectQuarterly(new long[]{device.getProjectId()}, new long[]{device.getId()},
                table, col, index, from, to);
    }


    @GetMapping("/yearly")
    public List<AggregateNumber> getYearly(@Principal Tenant tenant, @PathVariable("id") Device device,
                                           @PathVariable("analysisId") AnalysisTable table, @RequestParam String col,
                                           @RequestParam(required = false) int[] index,
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate from,
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        check(tenant, device,table);
        return analysisTableNumberQuery.selectYearly(new long[]{device.getProjectId()}, new long[]{device.getId()},
                table, col, index, from, to);
    }

    public void check(Tenant tenant, Device device, AnalysisTable table) {
        checkWithProject(device, tenant);
        Preconditions.checkState(device.getProductId().equals(table.getProductId()), "设备与分析产品不匹配！");
    }

}
