package com.yunmo.iot.api.operation;

import com.yunmo.boot.web.ApiPageable;
import com.yunmo.domain.common.*;
import com.yunmo.iot.domain.core.Device;
import com.yunmo.iot.domain.core.Product;
import com.yunmo.iot.domain.core.value.DeviceSearch;
import com.yunmo.iot.repository.jdbi.AlertsCountStatsQuery;
import com.yunmo.iot.repository.jdbi.AlertsCountStatsQueryJDBI;
import com.yunmo.iot.repository.jpa.DeviceJpaRepository;
import com.yunmo.iot.repository.jpa.ProductJpaRepository;
import com.yunmo.iot.repository.jpa.ProjectJpaRepository;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/operations")
public class OperationsResource {

    @Autowired
    DeviceJpaRepository deviceRepository;

    @Autowired
    ProductJpaRepository productRepository;

    @Autowired
    ProjectJpaRepository projectRepository;

    @Autowired
    Jdbi sqlJDBI;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static final class OperatorDevice {
        private Device device;
        private Optional<Product> product;
    }

    @ApiPageable
    @GetMapping("/devices")
    public Paged<?> getProjectDevice(@Principal Tenant tenant, DeviceSearch search, Pageable paging) {
        List<Long> projectIds = projectRepository.findByOperatorId(tenant.getDomain()).stream()
                .map(p->p.getId()).collect(Collectors.toList());
        Page<Device> devicePaged = deviceRepository.findByProjectIdIn(projectIds , paging);

        var joined = Linq.leftJoin(
                devicePaged.getContent(), productRepository::findById)
                .on(d->d.getProductId(), Linq.nullable(Product::getId), OperatorDevice::new)
                .toList();

        return Paged.of(devicePaged, joined);
    }


}
