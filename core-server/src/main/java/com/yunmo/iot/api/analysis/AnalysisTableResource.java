package com.yunmo.iot.api.analysis;


import com.yunmo.boot.web.ApiPageable;
import com.yunmo.domain.common.Asserts;
import com.yunmo.domain.common.Paged;
import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.iot.domain.analysis.AnalysisTable;
import com.yunmo.iot.domain.analysis.repository.AnalysisTableRepository;
import com.yunmo.iot.domain.analysis.service.AnalysisService;
import com.yunmo.iot.domain.analysis.value.AnalysisTableValue;
import com.yunmo.iot.domain.core.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/analysis")
public class AnalysisTableResource {
    @Autowired
    AnalysisService analysisService;

    @Autowired
    AnalysisTableRepository analysisTableRepository;

    @Transactional
    @PostMapping
    public AnalysisTable create(@Principal Tenant tenant, @PathVariable("productId") Product product,
                                @RequestBody AnalysisTableValue value) {
        Asserts.found(product, p->p.getTenantId().equals(tenant.getDomain()));
        return analysisService.create(product.getId(), value);
    }

    @ApiPageable
    @GetMapping
    public Page<AnalysisTable> getList(@Principal Tenant tenant,
                                       @PathVariable("productId") Product product, Pageable paging) {
        Asserts.found(product, p->p.getTenantId().equals(tenant.getDomain()));
        return analysisTableRepository.findAllByProductId(product.getId(), paging);
    }

    @ApiPageable
    @GetMapping("/all")
    public List<AnalysisTable> getAll(@Principal Tenant tenant,
                                      @PathVariable("productId") Product product) {
        Asserts.found(product, p->p.getTenantId().equals(tenant.getDomain()));
        return analysisTableRepository.findAllByProductId(product.getId());
    }

    @GetMapping("/{id}")
    public AnalysisTable getById(@Principal Tenant tenant, @PathVariable("productId") Product product,
                                 @PathVariable("id") AnalysisTable analysisTable) {
        Asserts.found(product, p->p.getTenantId().equals(tenant.getDomain())
         && analysisTable.getProductId().equals(product.getId()));
        return analysisTable;
    }

    @PutMapping("/{id}")
    public AnalysisTable update(@Principal Tenant tenant, @PathVariable("productId") Product product,
                                @PathVariable("id") AnalysisTable analysisTable, @RequestBody AnalysisTableValue value) {
        Asserts.found(product, p->p.getTenantId().equals(tenant.getDomain()));
        return analysisTableRepository.save(value.assignTo(analysisTable));
    }

    @DeleteMapping("/{id}")
    public void deleteByID(@Principal Tenant tenant,  @PathVariable("productId") Product product,
                           @PathVariable("id") AnalysisTable analysisTable) {
        Asserts.found(product, p->p.getTenantId().equals(tenant.getDomain())
                && analysisTable.getProductId().equals(product.getId()));
        analysisService.remove(analysisTable);
    }

}
