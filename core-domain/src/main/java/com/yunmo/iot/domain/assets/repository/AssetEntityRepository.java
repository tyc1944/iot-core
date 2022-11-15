package com.yunmo.iot.domain.assets.repository;

import com.yunmo.domain.common.EntityRepository;
import com.yunmo.iot.domain.assets.AssetEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AssetEntityRepository extends EntityRepository<AssetEntity, Long>  {
    Page<AssetEntity> findByProjectId(Long id, Pageable paging);

    Page<AssetEntity> findByProjectIdAndParentId(Long id, Long parent, Pageable paging);
    List<AssetEntity> findByProjectIdAndParentId(Long id, Long parent);

    AssetEntity findByDeviceId(long deviceId);
    List<AssetEntity> findByParentId(long id);
}
