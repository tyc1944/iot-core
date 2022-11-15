package com.yunmo.iot.domain.assets.repository;

import com.yunmo.domain.common.EntityRepository;
import com.yunmo.iot.domain.assets.AssetLabel;

import java.util.List;

public interface AssetLabelRepository extends EntityRepository<AssetLabel, Long>  {
    List<AssetLabel> findByAssetId(long assetId);
}
