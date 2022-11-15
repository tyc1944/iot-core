package com.yunmo.iot.api.core;

import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.boot.web.ApiPageable;
import com.yunmo.domain.common.Asserts;
import com.yunmo.domain.common.Paged;
import com.yunmo.iot.domain.core.Credentials;
import com.yunmo.iot.domain.core.DeviceHub;
import com.yunmo.iot.domain.core.Product;
import com.yunmo.iot.domain.core.value.DeviceHubValue;
import com.yunmo.iot.repository.jpa.HubJpaRepository;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

import static com.yunmo.iot.domain.core.CredentialsUtil.createCredentials;


@Api("产品下的接入点")
@Transactional
@RestController
@RequestMapping("/api/products/{productId}/hubs")
public class HubResource {
    @Autowired
    HubJpaRepository hubRepository;

    @GetMapping("/all")
    public List<DeviceHub> getAll(@Principal Tenant tenant,
                                  @PathVariable("productId") Product product) {
        Asserts.found(product, p->product.getTenantId().equals(tenant.getDomain()));
        return hubRepository.findAllByProductId(product.getId());
    }

    @ApiPageable
    @GetMapping
    public Page<DeviceHub> getList(@Principal Tenant tenant,
                                   @PathVariable("productId") Product product, Pageable paging) {
        Asserts.found(product, p->product.getTenantId().equals(tenant.getDomain()));
        return hubRepository.findAllByProductId(product.getId(), paging);
    }

    @PostMapping
    public DeviceHub create(@Principal Tenant tenant,
                            @PathVariable("productId") Product product,
                            @RequestBody DeviceHubValue value) {
        Asserts.found(product, p->product.getTenantId().equals(tenant.getDomain()));
        Credentials credentials = value.getCredentials();
        credentials.setSecret(credentials.getSecret().strip());
        DeviceHub hub = value.create();
        hub.setProductId(product.getId());
        hub = hubRepository.save(hub);

        if (StringUtils.isAnyBlank(credentials.getSecret(), credentials.getCrt())) {
            hub.setCredentials(createCredentials(hub.getId()));
            hubRepository.save(hub);
        }
        return hub;
    }

    @GetMapping("/{id}")
    public DeviceHub getById(@Principal Tenant tenant,
                             @PathVariable("productId") Product product,
                             @PathVariable("id") DeviceHub hub) {
        Asserts.found(product, p->product.getTenantId().equals(tenant.getDomain())
                &&hub.getProductId().equals(product.getId()));
        return hub;
    }

    @DeleteMapping("/{id}")
    public void deleteByID(@Principal Tenant tenant,
                           @PathVariable("productId") Product product,
                           @PathVariable("id") DeviceHub hub) {
        Asserts.found(product, p->product.getTenantId().equals(tenant.getDomain())
                &&hub.getProductId().equals(product.getId()));
        hubRepository.delete(hub);
    }

    @PutMapping("/{id}")
    public DeviceHub update(@Principal Tenant tenant,
                            @PathVariable("productId") Product product,
                            @PathVariable("id") DeviceHub hub,
                            @RequestBody DeviceHubValue value) {
        Asserts.found(product, p->product.getTenantId().equals(tenant.getDomain())
            &&hub.getProductId().equals(product.getId()));
        Credentials credentials = value.getCredentials();
        credentials.setSecret(credentials.getSecret().strip());
        return hubRepository.save(value.assignTo(hub));
    }
}
