package com.yunmo.iot.repository.jpa;

import com.yunmo.iot.domain.app.AppProfile;
import com.yunmo.iot.domain.app.repository.AppProfileRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppProfileJpaRepository extends JpaRepository<AppProfile, Long>, AppProfileRepository {
}
