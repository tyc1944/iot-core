package com.yunmo.iot.api.rule;

import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.boot.web.ApiPageable;
import com.yunmo.domain.common.Asserts;
import com.yunmo.domain.common.Paged;
import com.yunmo.iot.domain.core.Product;
import com.yunmo.iot.domain.rule.CommandTemplate;
import com.yunmo.iot.domain.rule.repository.CommandTemplateRepository;
import com.yunmo.iot.domain.rule.service.RuleCommandPreprocessService;
import com.yunmo.iot.domain.rule.value.CommandTemplateValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products/{productId}/commands")
public class CommandTemplateResource {
    @Autowired
    CommandTemplateRepository commandTemplateRepository;
    
    @Autowired
    RuleCommandPreprocessService preprocessService;

    @ApiPageable
    @GetMapping
    public Page<CommandTemplate> getList(@Principal Tenant tenant, @PathVariable("productId") Product product,
                                         Pageable paging) {
        Asserts.found(product, p->product.getTenantId().equals(tenant.getDomain()));
        return commandTemplateRepository.findByProductId(product.getId(), paging);
    }

    @PostMapping
    public CommandTemplate create(@Principal Tenant tenant, @PathVariable("productId") Product product,
                           @RequestBody CommandTemplateValue value) {
        CommandTemplate template = value.create();
        template.setProductId(product.getId());
        preprocessService.preprocess(template);
        return commandTemplateRepository.save(template);
    }



    @GetMapping("/{id}")
    public CommandTemplate getById(@Principal Tenant tenant, @PathVariable("productId") Product product,
                            @PathVariable("id") CommandTemplate template) {
        Asserts.found(product, p->p.getTenantId().equals(tenant.getDomain()) && template.getProductId().equals(p.getId()));
        return template;
    }

    @DeleteMapping("/{id}")
    public void deleteByID(@Principal Tenant tenant, @PathVariable("productId") Product product,
                           @PathVariable("id") CommandTemplate template) {
        Asserts.found(product, p->p.getTenantId().equals(tenant.getDomain())  && template.getProductId().equals(p.getId()));
        commandTemplateRepository.delete(template);
    }

    @PutMapping("/{id}")
    public CommandTemplate update(@Principal Tenant tenant, @PathVariable("productId") Product product,
                           @PathVariable("id") CommandTemplate template, @RequestBody CommandTemplateValue value) {
        Asserts.found(product, p->p.getTenantId().equals(tenant.getDomain())  && template.getProductId().equals(p.getId()));
        return commandTemplateRepository.save(
                preprocessService.preprocess(value.assignTo(template))
        );
    }
}
