package com.yunmo.iot.api.core;

import com.yunmo.domain.common.Asserts;
import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.iot.domain.core.Device;
import com.yunmo.iot.domain.core.Product;
import com.yunmo.iot.domain.core.value.DeviceSearch;
import com.yunmo.iot.repository.jpa.DeviceJpaRepository;
import com.yunmo.iot.repository.jpa.ProductJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;


@Transactional
@RestController
@RequestMapping("/api/products/{productId}")
public class ProductDeviceResource {
    @Autowired
    DeviceJpaRepository deviceRepository;
    @Autowired
    ProductJpaRepository productRepository;

    @GetMapping("/devices/select")
    public Page<Device> getProductDevice(@Principal Tenant tenant, @PathVariable("productId") Product product,
                                          @ModelAttribute DeviceSearch search, Pageable paging) {
        Asserts.found(product, p -> product.getTenantId().equals(tenant.getDomain()));
        return deviceRepository.findAll(Example.of(search.create()),paging);
    }

}
