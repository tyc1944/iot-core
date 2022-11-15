package com.yunmo.iot.api.rule;

import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.boot.web.ApiPageable;
import com.yunmo.domain.common.Asserts;
import com.yunmo.domain.common.Linq;
import com.yunmo.domain.common.Paged;
import com.yunmo.iot.domain.core.MessageSchema;
import com.yunmo.iot.domain.core.Product;
import com.yunmo.iot.domain.core.repository.MessageSchemaRepository;
import com.yunmo.iot.domain.rule.ActionType;
import com.yunmo.iot.domain.rule.RuleSpec;
import com.yunmo.iot.domain.rule.service.RuleCommandPreprocessService;
import com.yunmo.iot.domain.rule.value.RuleSpecSearch;
import com.yunmo.iot.domain.rule.value.RuleSpecUpdate;
import com.yunmo.iot.domain.rule.value.RuleSpecValue;
import com.yunmo.iot.repository.jpa.RuleSpecJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products/{productId}/rules")
@AllArgsConstructor
public class ProductRuleSpecResource {
    RuleSpecJpaRepository ruleRepository;
    
    RuleCommandPreprocessService rulePreprocessService;

    MessageSchemaRepository messageSchemaRepository;

    @ApiPageable
    @GetMapping
    public Paged<?> getList(@Principal Tenant tenant, @PathVariable("productId") Product product,
                            RuleSpecSearch search, Pageable paging) {
        Asserts.found(product, p->p.getTenantId().equals(tenant.getDomain()));
        RuleSpec example = search.create();
        example.setProductId(product.getId());
        example.setProjectId(0l);
        var rulePage = ruleRepository.findAll(Example.of(example), paging);
        var joined = Linq.leftJoin(rulePage.getContent(),"rule",
                messageSchemaRepository::findById,"schema")
                .on(RuleSpec::getSchemaId, Linq.nullable(MessageSchema::getId))
                .toList();
        return Paged.of(rulePage,joined);
    }

    @PostMapping
    public RuleSpec create(@Principal Tenant tenant, @PathVariable("productId") Product product,
                           @RequestBody RuleSpecValue value) {
        Asserts.found(product, p->p.getTenantId().equals(tenant.getDomain()));

        RuleSpec rule = value.create();
        rule.setTenantId(tenant.getId());
        rule.setProductId(product.getId());
        rule.setProjectId(0l);
        rule.setAction(ActionType.NOTIFICATION);
        rule = rulePreprocessService.preprocess(rule);
        return ruleRepository.save(rule);
    }

    @GetMapping("/{id}")
    public RuleSpec getById(@Principal Tenant tenant, @PathVariable("productId") Product product,
                            @PathVariable("id") RuleSpec rule) {
        Asserts.found(product, p->p.getTenantId().equals(tenant.getDomain()));
        Asserts.found(rule, r->r.getProductId().equals(product.getId()));
        return rule;
    }

    @DeleteMapping("/{id}")
    public void deleteByID(@Principal Tenant tenant, @PathVariable("productId") Product product,
                           @PathVariable("id") RuleSpec rule) {
        Asserts.found(product, p->p.getTenantId().equals(tenant.getDomain()));
        Asserts.found(rule, r->r.getProductId().equals(product.getId()));
        ruleRepository.delete(rule);
    }

    @PutMapping("/{id}")
    public RuleSpec update(@Principal Tenant tenant, @PathVariable("productId") Product product,
                           @PathVariable("id") RuleSpec rule, @RequestBody RuleSpecUpdate value) {
        Asserts.found(product, p->p.getTenantId().equals(tenant.getDomain()));
        Asserts.found(rule, r->r.getProductId().equals(product.getId()));
        value.setAction(ActionType.NOTIFICATION);
        rule = rulePreprocessService.preprocess(value.assignTo(rule));
        return ruleRepository.save(rule);
    }

    @PatchMapping("/{id}")
    public RuleSpec patch(@Principal Tenant tenant, @PathVariable("productId") Product product,
                           @PathVariable("id") RuleSpec rule, @RequestBody RuleSpecUpdate value) {
        Asserts.found(product, p->p.getTenantId().equals(tenant.getDomain()));
        Asserts.found(rule, r->r.getProductId().equals(product.getId()));
        value.setAction(ActionType.NOTIFICATION);
        rule = rulePreprocessService.preprocess(value.patchTo(rule));
        return ruleRepository.save(rule);
    }
}
