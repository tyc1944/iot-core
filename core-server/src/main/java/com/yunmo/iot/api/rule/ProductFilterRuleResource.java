package com.yunmo.iot.api.rule;

import com.yunmo.boot.web.ApiPageable;
import com.yunmo.domain.common.*;
import com.yunmo.iot.domain.core.MessageSchema;
import com.yunmo.iot.domain.core.Product;
import com.yunmo.iot.domain.core.repository.MessageSchemaRepository;
import com.yunmo.iot.domain.rule.FilterRule;
import com.yunmo.iot.domain.rule.service.RuleCommandPreprocessService;
import com.yunmo.iot.domain.rule.value.FilterRuleSearch;
import com.yunmo.iot.domain.rule.value.FilterRuleValue;
import com.yunmo.iot.domain.rule.value.FilterRuleUpdate;
import com.yunmo.iot.repository.jpa.FilterRuleJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products/{productId}/filters")
@AllArgsConstructor
public class ProductFilterRuleResource {
    FilterRuleJpaRepository ruleRepository;
    
    RuleCommandPreprocessService rulePreprocessService;

    MessageSchemaRepository messageSchemaRepository;

    @ApiPageable
    @GetMapping
    public Paged<?> getList(@Principal Tenant tenant, @PathVariable("productId") Product product,
                            FilterRuleSearch search, Pageable paging) {
        Asserts.found(product, p->p.getTenantId().equals(tenant.getDomain()));
        FilterRule example = search.create();
        example.setProductId(product.getId());
        var rulePage = ruleRepository.findAll(Example.of(example), paging);
        var joined = Linq.leftJoin(rulePage.getContent(),"rule",
                messageSchemaRepository::findById,"schema")
                .on(FilterRule::getSchemaId, Linq.nullable(MessageSchema::getId))
                .toList();
        return Paged.of(rulePage,joined);
    }

    @PostMapping
    public FilterRule create(@Principal Tenant tenant, @PathVariable("productId") Product product,
                           @RequestBody FilterRuleValue value) {
        Asserts.found(product, p->p.getTenantId().equals(tenant.getDomain()));

        FilterRule rule = value.create();
        rule.setTenantId(tenant.getId());
        rule.setProductId(product.getId());
        rule = rulePreprocessService.preprocess(rule);
        return ruleRepository.save(rule);
    }

    @GetMapping("/{id}")
    public FilterRule getById(@Principal Tenant tenant, @PathVariable("productId") Product product,
                            @PathVariable("id") FilterRule rule) {
        Asserts.found(product, p->p.getTenantId().equals(tenant.getDomain()));
        Asserts.found(rule, r->r.getProductId().equals(product.getId()));
        return rule;
    }

    @DeleteMapping("/{id}")
    public void deleteByID(@Principal Tenant tenant, @PathVariable("productId") Product product,
                           @PathVariable("id") FilterRule rule) {
        Asserts.found(product, p->p.getTenantId().equals(tenant.getDomain()));
        Asserts.found(rule, r->r.getProductId().equals(product.getId()));
        ruleRepository.delete(rule);
    }

    @PutMapping("/{id}")
    public FilterRule update(@Principal Tenant tenant, @PathVariable("productId") Product product,
                           @PathVariable("id") FilterRule rule, @RequestBody FilterRuleUpdate value) {
        Asserts.found(product, p->p.getTenantId().equals(tenant.getDomain()));
        Asserts.found(rule, r->r.getProductId().equals(product.getId()));
        rule = rulePreprocessService.preprocess(value.assignTo(rule));
        return ruleRepository.save(rule);
    }

    @PatchMapping("/{id}")
    public FilterRule patch(@Principal Tenant tenant, @PathVariable("productId") Product product,
                           @PathVariable("id") FilterRule rule, @RequestBody FilterRuleUpdate value) {
        Asserts.found(product, p->p.getTenantId().equals(tenant.getDomain()));
        Asserts.found(rule, r->r.getProductId().equals(product.getId()));
        rule = rulePreprocessService.preprocess(value.patchTo(rule));
        return ruleRepository.save(rule);
    }
}
