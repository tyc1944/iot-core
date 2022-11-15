package com.yunmo.iot.api.analysis;


import com.google.common.base.Preconditions;
import com.yunmo.domain.common.Linq;
import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.iot.domain.analysis.AnalysisTable;
import com.yunmo.iot.domain.analysis.repository.AnalysisTableNumberQuery;
import com.yunmo.iot.domain.analysis.value.ProjectRanking;
import com.yunmo.iot.domain.core.Product;
import com.yunmo.iot.repository.jpa.ProjectJpaRepository;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Api(tags="产品下分项目的统计排名")
@RestController
@RequestMapping("/api/products/{id}/analysis/{analysisId}/ranking")
public class ProductProjectRankingAnalysisResource {

    @Autowired
    AnalysisTableNumberQuery analysisTableNumberQuery;

    @Autowired
    ProjectJpaRepository projectRepository;


    @GetMapping("/day")
    public List<ProjectRanking> rankingByDay(@Principal Tenant tenant, @PathVariable("id") Product product,
                                             @PathVariable("analysisId") AnalysisTable table, @RequestParam String col,
                                             @RequestParam(required = false) int[] index,
                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate from,
                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        Preconditions.checkArgument(product.getTenantId().equals(tenant.getDomain()));
        List<ProjectRanking> ranking =  analysisTableNumberQuery.rankingProjectByDay(new long[]{}, new long[]{}, table, col, index, from, to);
        return enrich(ranking);
    }




    @GetMapping("/month")
    public List<ProjectRanking> rankingByMonth(@Principal Tenant tenant, @PathVariable("id") Product product,
                                               @PathVariable("analysisId") AnalysisTable table, @RequestParam String col,
                                               @RequestParam(required = false) int[] index,
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate from,
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        Preconditions.checkArgument(product.getTenantId().equals(tenant.getDomain()));
        List<ProjectRanking> ranking = analysisTableNumberQuery.rankingProjectByMonth(new long[]{}, new long[]{}, table, col, index, from, to);
        return enrich(ranking);
    }



    @GetMapping("/year")
    public List<ProjectRanking> rankingByYear(@Principal Tenant tenant, @PathVariable("id") Product product,
                                              @PathVariable("analysisId") AnalysisTable table, @RequestParam String col,
                                              @RequestParam(required = false) int[] index,
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate from,
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        Preconditions.checkArgument(product.getTenantId().equals(tenant.getDomain()));
        List<ProjectRanking> ranking =  analysisTableNumberQuery.rankingProjectByYear(new long[]{}, new long[]{}, table, col, index, from, to);
        return enrich(ranking);
    }


    private List<ProjectRanking> enrich(List<ProjectRanking> ranking) {
        return Linq.joinBatch(ranking, projectRepository::findAllById).on(r->r.get__project_id(), p->p.getId(), (r,project)->{
            r.setName(project.getName());
            return r;
        }).toList().stream().filter(p->p.getName()!=null).collect(Collectors.toList());
    }

}
