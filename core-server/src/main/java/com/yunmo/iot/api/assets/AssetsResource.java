package com.yunmo.iot.api.assets;


import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.boot.web.ApiPageable;
import com.yunmo.domain.common.Asserts;
import com.yunmo.domain.common.Paged;
import com.yunmo.iot.domain.assets.AssetEntity;
import com.yunmo.iot.domain.assets.AssetLabel;
import com.yunmo.iot.domain.assets.repository.AssetLabelRepository;
import com.yunmo.iot.domain.assets.value.AssetEntitySearch;
import com.yunmo.iot.domain.assets.value.AssetEntityValue;
import com.yunmo.iot.domain.core.Project;
import com.yunmo.iot.repository.jpa.AssetEntityJpaRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
@RestController
@RequestMapping("/api/projects/{projectId}/assets")
public class AssetsResource {
    @Autowired
    AssetEntityJpaRepository assetEntityRepository;

    @Autowired
    AssetLabelRepository assetLabelRepository;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static final class LabeledAsset {
        private AssetEntity asset;
        private List<AssetLabel> labels;
    }

    @ApiPageable
    @GetMapping
    public Page<LabeledAsset> getList(@Principal Tenant tenant,
                                      @PathVariable("projectId") Project project,
                                      AssetEntitySearch search, Pageable paging) {
        Asserts.found(project, p->p.getTenantId().equals(tenant.getDomain()));
        AssetEntity example = search.create();
        example.setProjectId(project.getId());
        return assetEntityRepository.findAll(Example.of(example), paging)
                .map(a->new LabeledAsset(a, assetLabelRepository.findByAssetId(a.getId())));
    }

    @PostMapping
    public AssetEntity create(@Principal Tenant tenant,
                              @PathVariable("projectId") Project project,
                              @RequestBody AssetEntityValue value,
                              @RequestParam Optional<String> path) {
        Asserts.found(project, p->p.getTenantId().equals(tenant.getDomain()));
        AssetEntity asset = value.create();
        asset.setProjectId(project.getId());

        if(value.getParentId() == null) {
            asset.setParentId(0l);
        } else if(path.isPresent()) {
            asset.setPath(path.get());
        } else {
            AssetEntity parent = assetEntityRepository.findById(asset.getParentId()).get();
            asset.setPath(Optional.ofNullable(parent.getPath())
                    .map(p->String.join("/",p,parent.getId().toString()))
                    .orElse(parent.getId().toString()));
        }


        return assetEntityRepository.save(asset);
    }

    @GetMapping("/{id}")
    public AssetEntity getById(@Principal Tenant tenant,
                               @PathVariable("projectId") Project project,
                               @PathVariable("id") AssetEntity asset) {
        Asserts.found(project, p->p.getTenantId().equals(tenant.getDomain())
                &&asset.getProjectId().equals(project.getId()));
        return asset;
    }

    @DeleteMapping("/{id}")
    public void deleteByID(@Principal Tenant tenant,
                           @PathVariable("projectId") Project project,
                           @PathVariable("id") AssetEntity asset) {
        Asserts.found(project, p->p.getTenantId().equals(tenant.getDomain())
                &&asset.getProjectId().equals(project.getId()));
        assetEntityRepository.delete(asset);
    }

    @PutMapping("/{id}")
    public AssetEntity update(@Principal Tenant tenant,
                              @PathVariable("projectId") Project project,
                              @PathVariable("id") AssetEntity asset,
                              @RequestBody AssetEntityValue value) {
        Asserts.found(project, p->p.getTenantId().equals(tenant.getDomain())
                &&asset.getProjectId().equals(project.getId()));
        if(value.getParentId() == null) {
            asset.setParentId(0l);
        }
        return assetEntityRepository.save(value.assignTo(asset));
    }
}
