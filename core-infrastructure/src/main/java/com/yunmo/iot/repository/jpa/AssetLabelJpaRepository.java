package com.yunmo.iot.repository.jpa;

import com.yunmo.iot.domain.assets.AssetLabel;
import com.yunmo.iot.domain.assets.repository.AssetLabelRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetLabelJpaRepository extends JpaRepository<AssetLabel, Long>, AssetLabelRepository {
    @Cacheable(cacheNames = "AssetLabels", key = "#p0")
    List<AssetLabel> findByAssetId(long assetId);

    @CacheEvict(cacheNames = { "AssetLabels" }, key = "#p0.assetId")
    void delete(AssetLabel entity);

    @CacheEvict(cacheNames = { "AssetLabels" }, key = "#p0.assetId")
    <S extends AssetLabel> S save(S entity);
}
