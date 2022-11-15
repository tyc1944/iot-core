package com.yunmo.iot.api.core;

import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.iot.domain.core.Product;
import com.yunmo.iot.repository.jpa.ProductJpaRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.List;


@Api("目录信息接口")
@Transactional
@RestController
@RequestMapping("/api/catalogs")
public class CatalogResource {

    @Autowired
    ProductJpaRepository productRepository;

    @ApiOperation(value = "获取所有产品信息", notes = "暂时没有任何约束，就是所有产品")
    @GetMapping("/products")
    public List<Product> getAllProduct(@Principal Tenant tenant) {
        return productRepository.findAll();
    }
}
