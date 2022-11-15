package com.yunmo.iot.api.core;

import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.domain.common.Asserts;
import com.yunmo.iot.domain.core.MessageSchema;
import com.yunmo.iot.domain.core.Product;
import com.yunmo.iot.domain.core.service.ProductChannelService;
import com.yunmo.iot.domain.core.value.MessageSchemaValue;
import com.yunmo.iot.repository.jpa.MessageSchameJpaRepository;
import com.yunmo.iot.schema.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;


@RestController
@RequestMapping("/api/products/{id}/channels")
public class ProductChannelResource {
    @Autowired
    MessageSchameJpaRepository schemaRepository;

    @Autowired
    ProductChannelService productChannelService;

    @Transactional
    @PutMapping("/{channel}")
    public MessageSchema upsert(@Principal Tenant tenant, @PathVariable("id") Product product,
                                @PathVariable String channel, @RequestBody MessageSchemaValue value) {
        Asserts.found(product, p->product.getTenantId().equals(tenant.getDomain()));
        return productChannelService.upsertChannel(product, channel, value);
    }

    @GetMapping
    public List<MessageSchema> getByAll(@Principal Tenant tenant, @PathVariable("id") Product product) {
        Asserts.found(product, p->product.getTenantId().equals(tenant.getDomain()));
        return schemaRepository.findByProductId(product.getId());
    }

    @GetMapping("/characterized")
    public List<MessageSchema> getCharacterizedByAll(@Principal Tenant tenant, @PathVariable("id") Product product) {
        Asserts.found(product, p->product.getTenantId().equals(tenant.getDomain()));
        List<MessageSchema>  schemas = schemaRepository.findByProductId(product.getId());
        schemas.forEach(s->{
            s.setRecordSchema(s.getRecordSchema().characterized().orElse(new Schema()));
        });
        return schemas;
    }

    @GetMapping("/{channel}")
    public MessageSchema getByName(@Principal Tenant tenant, @PathVariable("id") Product product,
                                   @PathVariable String channel) {
        Asserts.found(product, p->product.getTenantId().equals(tenant.getDomain()));
        return schemaRepository.findByProductIdAndChannel(product.getId(), channel).get();
    }

    @Transactional
    @DeleteMapping("/{channel}")
    public void deleteByID(@Principal Tenant tenant, @PathVariable("id") Product product,
                           @PathVariable String channel) {
        Asserts.found(product, p->product.getTenantId().equals(tenant.getDomain()));
        MessageSchema schema = schemaRepository.findByProductIdAndChannel(product.getId(), channel).get();
        schemaRepository.delete(schema);
    }
}
