package com.yunmo.iot.api.core;

import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.boot.web.ApiPageable;
import com.yunmo.domain.common.Asserts;
import com.yunmo.domain.common.Problems;
import com.yunmo.iot.domain.core.Project;
import com.yunmo.iot.domain.core.repository.DeviceRepository;
import com.yunmo.iot.domain.core.value.ProjectValue;
import com.yunmo.iot.repository.jpa.ProjectJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;


@Transactional
@RestController
@RequestMapping("/api/projects")
public class ProjectResource {
    @Autowired
    ProjectJpaRepository projectRepository;

    @Autowired
    DeviceRepository deviceRepository;

    @GetMapping("/all")
    public List<Project> getAll(@Principal Tenant tenant) {
        return projectRepository.findByTenantId(tenant.getDomain());
    }

    @ApiPageable
    @GetMapping
    public Page<Project> getList(@Principal Tenant tenant, Pageable paging) {
        return projectRepository.findByTenantId(tenant.getId(), paging);
    }

    @PostMapping
    public Project create(@Principal Tenant tenant, @RequestBody ProjectValue value) {
        Project project = value.create();
        project.setTenantId(tenant.getDomain());
        return projectRepository.save(project);
    }

    @GetMapping("/{id}")
    public Project getById(@Principal Tenant tenant, @PathVariable("id") Project project) {
        Asserts.found(project, p->project.getTenantId().equals(tenant.getDomain()));
        return project;
    }

    @DeleteMapping("/{id}")
    public void deleteByID(@Principal Tenant tenant, @PathVariable("id") Project project) {
        Asserts.found(project, p->project.getTenantId().equals(tenant.getDomain()));
        Long existingDevice = deviceRepository.countByProjectId(project.getId());
        Problems.ensure(existingDevice == 0, "项目下设备数大于0，不可删除");
        projectRepository.delete(project);
    }

    @PutMapping("/{id}")
    public Project update(@Principal Tenant tenant, @PathVariable("id") Project project, @RequestBody ProjectValue value) {
        Asserts.found(project, p->project.getTenantId().equals(tenant.getDomain()));
        return projectRepository.save(value.assignTo(project));
    }

    @PatchMapping("/{id}")
    public Project patch(@Principal Tenant tenant, @PathVariable("id") Project project, @RequestBody ProjectValue value) {
        Asserts.found(project, p->project.getTenantId().equals(tenant.getDomain()));
        return projectRepository.save(value.patchTo(project));
    }
}
