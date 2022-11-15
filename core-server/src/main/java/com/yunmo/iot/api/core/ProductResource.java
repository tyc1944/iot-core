package com.yunmo.iot.api.core;

import com.yunmo.boot.web.ApiPageable;
import com.yunmo.domain.common.Asserts;
import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.iot.domain.core.Product;
import com.yunmo.iot.domain.core.value.ProductValue;
import com.yunmo.iot.repository.jpa.DeviceJpaRepository;
import com.yunmo.iot.repository.jpa.ProductJpaRepository;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;


@Transactional
@RestController
@RequestMapping("/api/products")
public class ProductResource {
    @Autowired
    DeviceJpaRepository deviceRepository;
    @Autowired
    ProductJpaRepository productRepository;

    @ApiOperation(value = "当前域的所有产品信息", notes = "获取平台所有产品信息的接口在Catalog目录接口种")
    @GetMapping("/all")
    public List<Product> getAll(@Principal Tenant tenant) {
        return productRepository.findByTenantId(tenant.getDomain());
    }

    @ApiPageable
    @GetMapping
    public Page<Product> getList(@Principal Tenant tenant, Pageable paging) {
        return productRepository.findByTenantId(tenant.getId(), paging);
    }

    @PostMapping
    public Product create(@Principal Tenant tenant, @RequestBody ProductValue value) {
        Product product = value.create();
        product.setTenantId(tenant.getDomain());
        return productRepository.save(product);
    }

    @GetMapping("/{id}")
    public Product getById(@Principal Tenant tenant, @PathVariable("id") Product product) {
        Asserts.found(product, p->product.getTenantId().equals(tenant.getDomain()));
        return product;
    }

    @DeleteMapping("/{id}")
    public void deleteByID(@Principal Tenant tenant, @PathVariable("id") Product product) {
        Asserts.found(product, p->product.getTenantId().equals(tenant.getDomain()));
        productRepository.delete(product);
    }

    @PutMapping("/{id}")
    public Product update(@Principal Tenant tenant, @PathVariable("id") Product product, @RequestBody ProductValue value) {
        Asserts.found(product, p->product.getTenantId().equals(tenant.getDomain()));
        return productRepository.save(value.assignTo(product));
    }

}
