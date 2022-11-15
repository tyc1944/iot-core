package com.yunmo.iot.repository.jpa;

import com.yunmo.iot.domain.core.DeviceOwnerShip;
import com.yunmo.iot.domain.core.repository.DeviceOwnerShipRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceOwnerShipJpaRepository extends JpaRepository<DeviceOwnerShip, DeviceOwnerShip.ID>, DeviceOwnerShipRepository {

}
