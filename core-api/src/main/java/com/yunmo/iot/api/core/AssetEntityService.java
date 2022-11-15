package com.yunmo.iot.api.core;

import com.yunmo.iot.domain.assets.AssetEntity;
import com.yunmo.iot.domain.assets.value.AssetEntityValue;
import io.genrpc.annotation.GrpcService;

import java.util.List;

@GrpcService
public interface AssetEntityService {

    AssetEntity findByDeviceId(long deviceId);
    AssetEntity findById(long id);
    AssetEntity create(AssetEntityValue assetEntity);
    List<AssetEntity> findByParentId(long id);
}
