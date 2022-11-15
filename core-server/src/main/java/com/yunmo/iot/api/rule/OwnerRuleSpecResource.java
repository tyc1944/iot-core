package com.yunmo.iot.api.rule;

import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.boot.web.ApiPageable;
import com.yunmo.domain.common.Asserts;
import com.yunmo.domain.common.Linq;
import com.yunmo.domain.common.Paged;
import com.yunmo.iot.domain.core.MessageSchema;
import com.yunmo.iot.domain.core.repository.MessageSchemaRepository;
import com.yunmo.iot.domain.core.service.DeviceOwnerShipService;
import com.yunmo.iot.domain.rule.RuleSpec;
import com.yunmo.iot.domain.rule.service.RuleCommandPreprocessService;
import com.yunmo.iot.domain.rule.value.RuleSpecSearch;
import com.yunmo.iot.domain.rule.value.RuleSpecValue;
import com.yunmo.iot.repository.jpa.RuleSpecJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me/rules")
public class OwnerRuleSpecResource {
    @Autowired
    RuleSpecJpaRepository ruleRepository;
    
    @Autowired
    RuleCommandPreprocessService rulePreprocessService;

    @Autowired
    DeviceOwnerShipService deviceOwnerShipService;

    @Autowired
    MessageSchemaRepository messageSchemaRepository;

    @ApiPageable
    @GetMapping
    public Paged<?> getList(@Principal Tenant tenant,
                                   RuleSpecSearch search, Pageable paging) {
        RuleSpec example = search.create();
        example.setTenantId(tenant.getId());
        var rulePage = ruleRepository.findAll(Example.of(search.create()), paging);
        var joined = Linq.leftJoin(rulePage.getContent(),"rule",
                messageSchemaRepository::findById,"schema")
                .on(RuleSpec::getSchemaId, Linq.nullable(MessageSchema::getId))
                .toList();
        return Paged.of(rulePage,joined);
    }

    @PostMapping
    public RuleSpec create(@Principal Tenant tenant, @RequestBody RuleSpecValue value) {
        deviceOwnerShipService.isOwner(tenant.getId(), value.getDeviceId());
        RuleSpec rule = value.create();
        rule.setTenantId(tenant.getId());
        rule = rulePreprocessService.preprocess(rule);
        return ruleRepository.save(rule);
    }

    @GetMapping("/{id}")
    public RuleSpec getById(@Principal Tenant tenant, @PathVariable("id") RuleSpec rule) {
        Asserts.found(rule, r->r.getTenantId().equals(tenant.getId()));
        return rule;
    }

    @DeleteMapping("/{id}")
    public void deleteByID(@Principal Tenant tenant, @PathVariable("id") RuleSpec rule) {
        Asserts.found(rule, r->r.getTenantId().equals(tenant.getId()));
        ruleRepository.delete(rule);
    }

    @PutMapping("/{id}")
    public RuleSpec update(@Principal Tenant tenant, @PathVariable("id") RuleSpec rule, @RequestBody RuleSpecValue value) {
//        Asserts.found(rule, r->r.getTenantId().equals(tenant.getId()));//todo 科兴项目修改
        rule = rulePreprocessService.preprocess(value.assignTo(rule));
        return ruleRepository.save(rule);
    }

    @PatchMapping("/{id}")
    public RuleSpec patch(@Principal Tenant tenant, @PathVariable("id") RuleSpec rule, @RequestBody RuleSpecValue value) {
        Asserts.found(rule, r->r.getTenantId().equals(tenant.getId()));
        rule = rulePreprocessService.preprocess(value.patchTo(rule));
        return ruleRepository.save(rule);
    }
}
