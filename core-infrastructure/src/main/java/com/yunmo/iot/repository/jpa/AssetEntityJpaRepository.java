package com.yunmo.iot.repository.jpa;

import com.yunmo.iot.domain.assets.AssetEntity;
import com.yunmo.iot.domain.assets.repository.AssetEntityRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;

public interface AssetEntityJpaRepository extends JpaRepository<AssetEntity, Long>, AssetEntityRepository {
}
