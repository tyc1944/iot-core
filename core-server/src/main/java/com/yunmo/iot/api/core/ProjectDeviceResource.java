package com.yunmo.iot.api.core;

import com.google.common.primitives.Longs;
import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.boot.web.ApiPageable;
import com.yunmo.domain.common.Asserts;
import com.yunmo.domain.common.Linq;
import com.yunmo.domain.common.Paged;
import com.yunmo.iot.api.operation.OperationsResource;
import com.yunmo.iot.domain.core.*;
import com.yunmo.iot.domain.core.value.DeviceSearch;
import com.yunmo.iot.repository.cassandra.TelemetryRecordC7aRepository;
import com.yunmo.iot.repository.jpa.DeviceJpaRepository;
import com.yunmo.iot.repository.jpa.HubJpaRepository;
import com.yunmo.iot.repository.jpa.ProductJpaRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Api(tags = "项目下的设备信息查询")
@RestController
@RequestMapping("/api/projects/{projectId}")
public class ProjectDeviceResource {

    @Autowired
    HubJpaRepository hubRepository;

    @Autowired
    DeviceJpaRepository deviceRepository;

    @Autowired
    ProductJpaRepository productRepository;

    @Data
    @NoArgsConstructor
    @Accessors(chain = true)
    public static final class ProjectDevice {
        private Device device;
        private DeviceHub hub;
        private Optional<Product> product;

        public ProjectDevice(Device device, DeviceHub hub) {
            this.device = device;
            this.hub = hub;
        }
    }

    @PostMapping("/devices/select")
    public List<Device> selectByIdOrLocalId(@Principal Tenant tenant, @PathVariable("projectId") Project project,
                                            String q) {
        Asserts.found(project, p->project.getTenantId().equals(tenant.getDomain()));
        return deviceRepository.findByProjectIdAndIdOrProjectIdAndLocalId(project.getId(), Longs.tryParse(q), project.getId(), q);
    }

    @ApiPageable
    @GetMapping("/devices")
    public Paged<?> getProjectDevice(@Principal Tenant tenant, @PathVariable("projectId") Project project,
                                                      DeviceSearch search, Pageable paging) {
        Asserts.found(project, p->project.getTenantId().equals(tenant.getDomain()));

        Page<Device> devicePaged = deviceRepository.findAll(Example.of(search.create()), paging);

        var joined = Linq.leftJoinBatch(
                devicePaged.getContent(), hubRepository::findAllById)
                .on(Device::getHubId, DeviceHub::getId, ProjectDevice::new)
                .leftJoin(productRepository::findById)
                .on(d->d.getDevice().getProductId(), Linq.nullable(Product::getId), ProjectDevice::setProduct)
                .toList();

        return Paged.of(devicePaged, joined);
    }

    @ApiOperation("项目下设备对应的所有产品")
    @ApiPageable
    @GetMapping("/devices/products")
    public Page<Product> getAllDeviceProducts(@Principal Tenant tenant, @PathVariable("projectId") Project project, Pageable paging) {
        Asserts.found(project, p->project.getTenantId().equals(tenant.getDomain()));

        //同一个项目下的设备产品数不会太多
        List<Long> productIds = deviceRepository.findProductByProjectId(project.getId());
        if(productIds.isEmpty()) {
            return Page.empty();
        }

        return productRepository.findByIdIn(productIds, paging);
    }


    @Autowired
    TelemetryRecordC7aRepository telemetryRecordRepository;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static final class OperatorDevice {
        private Device device;
        private Optional<GenericTelemetryRecord> telemetry;
    }

    @ApiOperation("查询带有最新一条数据的设备列表")
    @ApiPageable
    @GetMapping("/devices/latest")
    public Paged<OperatorDevice> getProjectDevice(@PathVariable("projectId") Long projectId,
                                                                     @RequestParam(defaultValue = "default") String channel,
                                                                     DeviceSearch search, Pageable paging) {
        Device  example = search.create();
        example.setProjectId(projectId);
        Page<Device> devicePaged = deviceRepository.findAll(Example.of(example) , paging);

        var joined = Linq.leftJoin(
                devicePaged.getContent(), (Function<Long, Optional<GenericTelemetryRecord>>) deviceId -> telemetryRecordRepository.findLatest(deviceId, search.getProductId(), channel))
                .on(d->d.getId(), Linq.nullable(GenericTelemetryRecord::getDeviceId), OperatorDevice::new)
                .toList();

        return Paged.of(devicePaged, joined);
    }

    private void checkProjectAuthorize(Tenant tenant, Project project) {
        Asserts.found(project, p->project.getTenantId().equals(tenant.getDomain())
        || project.getOperatorId().equals(tenant.getDomain()));
    }
}
