package com.yunmo.iot.repository.jpa;

import com.yunmo.iot.domain.ota.ProductFirmware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductFirmwareJpaRepository extends JpaRepository<ProductFirmware, Long> {

    Page<ProductFirmware> findByProductId(Long productId, Pageable paging);

}
