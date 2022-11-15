package com.yunmo.iot.api.assets;


import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Asserts;
import com.yunmo.iot.domain.assets.AssetLabel;
import com.yunmo.iot.domain.assets.repository.AssetLabelRepository;
import com.yunmo.iot.domain.assets.value.AssetLabelValue;
import com.yunmo.iot.domain.core.Project;
import com.yunmo.domain.common.Tenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@Transactional
@RestController
@RequestMapping("/api/projects/{projectId}/assets/{assetId}/labels")
public class AssetLabelsResource {
    @Autowired
    AssetLabelRepository assetLabelRepository;


    @PostMapping
    public AssetLabel create(@Principal Tenant tenant,
                              @PathVariable("projectId") Project project,
                              @PathVariable Long assetId,
                              @RequestBody AssetLabelValue value) {
        Asserts.found(project, p->p.getTenantId().equals(tenant.getDomain()));
        AssetLabel label = value.create();
        label.setProjectId(project.getId());
        label.setAssetId(assetId);
        return assetLabelRepository.save(label);
    }


    @DeleteMapping("/{id}")
    public void deleteByID(@Principal Tenant tenant,
                           @PathVariable("projectId") Project project,
                           @PathVariable("id") AssetLabel label) {
        Asserts.found(project, p->p.getTenantId().equals(tenant.getDomain())
                &&label.getProjectId().equals(project.getId()));
        assetLabelRepository.delete(label);
    }
}
