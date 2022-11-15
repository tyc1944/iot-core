package com.yunmo.iot.api.analysis;


import com.google.common.base.Preconditions;
import com.yunmo.domain.common.Linq;
import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.iot.domain.analysis.AnalysisTable;
import com.yunmo.iot.domain.analysis.repository.AnalysisTableNumberQuery;
import com.yunmo.iot.domain.analysis.value.DeviceRanking;
import com.yunmo.iot.domain.analysis.value.ProjectRanking;
import com.yunmo.iot.domain.core.Project;
import com.yunmo.iot.domain.core.repository.DeviceRepository;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@Api(tags="项目下分设备的统计排名")
@RestController
@RequestMapping("/api/projects/{id}/analysis/{analysisId}/ranking")
public class ProjectDeviceRankingAnalysisResource {

    @Autowired
    AnalysisTableNumberQuery analysisTableNumberQuery;

    @Autowired
    DeviceRepository deviceRepository;


    @GetMapping("/day")
    public List<DeviceRanking> rankingByDay(@Principal Tenant tenant, @PathVariable("id") Project project,
                                             @PathVariable("analysisId") AnalysisTable table, @RequestParam String col,
                                             @RequestParam(required = false) int[] index,
                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate from,
                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        Preconditions.checkArgument(project.getTenantId().equals(tenant.getDomain()));
        List<DeviceRanking> ranking =  analysisTableNumberQuery.rankingDeviceByDay(new long[]{project.getId()}, new long[]{}, table, col, index, from, to);
        return enrich(ranking);
    }




    @GetMapping("/month")
    public List<DeviceRanking> rankingByMonth(@Principal Tenant tenant, @PathVariable("id") Project project,
                                               @PathVariable("analysisId") AnalysisTable table, @RequestParam String col,
                                               @RequestParam(required = false) int[] index,
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate from,
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        Preconditions.checkArgument(project.getTenantId().equals(tenant.getDomain()));
        List<DeviceRanking> ranking = analysisTableNumberQuery.rankingDeviceByMonth(new long[]{project.getId()}, new long[]{}, table, col, index, from, to);
        return enrich(ranking);
    }



    @GetMapping("/year")
    public List<DeviceRanking> rankingByYear(@Principal Tenant tenant, @PathVariable("id") Project project,
                                              @PathVariable("analysisId") AnalysisTable table, @RequestParam String col,
                                              @RequestParam(required = false) int[] index,
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate from,
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        Preconditions.checkArgument(project.getTenantId().equals(tenant.getDomain()));
        List<DeviceRanking> ranking =  analysisTableNumberQuery.rankingDeviceByYear(new long[]{project.getId()}, new long[]{}, table, col, index, from, to);
        return enrich(ranking);
    }


    private List<DeviceRanking> enrich(List<DeviceRanking> ranking) {
        return Linq.joinBatch(ranking, deviceRepository::findAllById)
                .on(r->r.get__device_id(), p->p.getId(), (r,d)-> {
                    r.setDevice(d);
                    return r;
                }).toList();
    }

}
