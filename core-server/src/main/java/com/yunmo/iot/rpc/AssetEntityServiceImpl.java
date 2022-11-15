package com.yunmo.iot.rpc;

import com.yunmo.iot.api.core.AssetEntityService;
import com.yunmo.iot.domain.assets.AssetEntity;
import com.yunmo.iot.domain.assets.repository.AssetEntityRepository;
import com.yunmo.iot.domain.assets.value.AssetEntityValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetEntityServiceImpl implements AssetEntityService {

    @Autowired
    AssetEntityRepository assetEntityRepository;

    @Override
    public AssetEntity findByDeviceId(long deviceId) {
        return assetEntityRepository.findByDeviceId(deviceId);
    }

    @Override
    public AssetEntity findById(long id) {
        return assetEntityRepository.findById(id).get();
    }

    @Override
    public AssetEntity create(AssetEntityValue assetEntity) {
        return assetEntityRepository.save(assetEntity.create());
    }

    @Override
    public List<AssetEntity> findByParentId(long id) {
        return assetEntityRepository.findByParentId(id);
    }

}
