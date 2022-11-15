package com.yunmo.iot.api.ota;

import com.google.common.collect.Maps;
import com.yunmo.domain.common.Asserts;
import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.generator.annotation.ValueField;
import com.yunmo.iot.api.service.FileStorageExtendService;
import com.yunmo.iot.domain.core.Device;
import com.yunmo.iot.domain.core.Product;
import com.yunmo.iot.domain.file.UploadedFilePath;
import com.yunmo.iot.domain.ota.DeviceOTARecord;
import com.yunmo.iot.domain.ota.Firmware;
import com.yunmo.iot.domain.ota.ProductFirmware;
import com.yunmo.iot.repository.jpa.DeviceJpaRepository;
import com.yunmo.iot.repository.jpa.DeviceOTARecordJpaRepository;
import com.yunmo.iot.repository.jpa.ProductFirmwareJpaRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

@Transactional
@RestController
@RequestMapping("/api/products/{product}/firmware")
public class ProductFirmwareResource {

    @Autowired
    ProductFirmwareJpaRepository productFirmwareRepository;

    @Autowired
    DeviceJpaRepository deviceRepository;

    @Autowired
    DeviceOTARecordJpaRepository deviceOTARecordRepository;

    @Autowired
    FileStorageExtendService fileStorageService;

    @Value("${file.host}")
    private String fileHost;

    @GetMapping
    public Page<ProductFirmware> list(@Principal Tenant tenant, @PathVariable Product product, Pageable paging) {
        Asserts.found(product, p -> product.getTenantId().equals(tenant.getDomain()));
        return productFirmwareRepository.findByProductId(product.getId(), paging);
    }

    @Data
    public static class FirmwareUpload {
        private String name;

        private String description;

        private String version;

        private String location;
    }

    @PostMapping
    public ProductFirmware upload(@Principal Tenant tenant, @PathVariable Product product, @RequestBody FirmwareUpload uploaded) throws IOException, NoSuchAlgorithmException {
        Asserts.found(product, p -> product.getTenantId().equals(tenant.getDomain()));
        UploadedFilePath path = fileStorageService.storageInfo(uploaded.getLocation());

        ProductFirmware productFirmware = ProductFirmware.builder()
                .firmware(Firmware.builder()
                        .name(uploaded.name)
                        .version(uploaded.version)
                        .description(uploaded.description)
                        .url(fileHost.concat(uploaded.getLocation()))
                        .md5(path.getMd5())
                        .build())
                .productId(product.getId()).build();

        return productFirmwareRepository.save(productFirmware);
    }

    @PutMapping("/{firmware}/ota/devices")
    public void batchOTA(@Principal Tenant tenant, @PathVariable Product product,
                         @PathVariable ProductFirmware firmware, @RequestBody List<Long> devices) {
        Asserts.found(product, p -> product.getTenantId().equals(tenant.getDomain()));
        deviceRepository.findAllById(devices).forEach(device -> {
            device.updateConfig(Map.of("firmware", firmware.getFirmware()));
            DeviceOTARecord record = new DeviceOTARecord()
                    .setDeviceId(device.getId())
                    .setFirmware(firmware.getFirmware());
            deviceOTARecordRepository.save(record);
        });
    }


}