package com.yunmo.iot.api.app;

import com.google.common.base.Preconditions;
import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.boot.web.ApiPageable;
import com.yunmo.domain.common.Asserts;
import com.yunmo.domain.common.Paged;
import com.yunmo.iot.domain.app.AppProfile;
import com.yunmo.iot.domain.app.repository.AppProfileRepository;
import com.yunmo.iot.domain.app.value.AppProfileValue;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;


@Transactional
@RestController
@RequestMapping("/api/apps")
public class AppProfileResource {

    @Autowired
    AppProfileRepository appProfileRepository;

    @PostMapping
    public AppProfile create(@Principal Tenant tenant, @RequestBody AppProfileValue value) {
        if (value.getApprovedDeviceIds() != null) {
            Preconditions.checkArgument(value.getApprovedDeviceIds().length < 100);
        }

        if (value.getApprovedProjectIds() != null) {
            Preconditions.checkArgument(value.getApprovedProjectIds().length < 100);
        }
        AppProfile app = value.create();
        app.setTenantId(tenant.getDomain());
        app.setAppKey(RandomStringUtils.randomAlphanumeric(32));
        app.resetToken();
        return appProfileRepository.save(app);
    }

    @ApiPageable
    @GetMapping
    public Paged<AppProfile> getList(@Principal Tenant tenant,Pageable paging) {
        return Paged.of(appProfileRepository.findByTenantId(tenant.getDomain(), paging));
    }

    @GetMapping("/{id}")
    public AppProfile getById(@Principal Tenant tenant, @PathVariable("id") AppProfile app) {
        Asserts.found(app, a -> a.getTenantId().equals(tenant.getId()));
        return app;
    }

    @GetMapping("/{id}/token")
    public String getToken(@Principal Tenant tenant, @PathVariable("id") AppProfile app) {
        Asserts.found(app, a -> a.getTenantId().equals(tenant.getId()));
        return app.getToken();
    }

    @PutMapping("/{id}")
    public void update(@Principal Tenant tenant, @PathVariable("id") AppProfile app, @RequestBody AppProfileValue value) {
        Asserts.found(app, a -> a.getTenantId().equals(tenant.getId()));
        appProfileRepository.save(value.assignTo(app));
    }

    @DeleteMapping("/{id}")
    public void deleteByID(@Principal Tenant tenant, @PathVariable("id") AppProfile app) {
        Asserts.found(app, a -> a.getTenantId().equals(tenant.getId()));
        appProfileRepository.delete(app);
    }

    @PostMapping("/{id}/token/reset")
    public String resetToken(@Principal Tenant tenant, @PathVariable("id") AppProfile app) {
        Asserts.found(app, a -> a.getTenantId().equals(tenant.getId()));
        app.resetToken();
        appProfileRepository.save(app);
        return app.getToken();
    }

}
