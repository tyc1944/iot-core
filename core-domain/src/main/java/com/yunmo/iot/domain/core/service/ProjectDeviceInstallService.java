package com.yunmo.iot.domain.core.service;

import com.yunmo.domain.common.Asserts;
import com.yunmo.domain.common.Problems;
import com.yunmo.domain.common.Tenant;
import com.yunmo.iot.domain.core.Device;
import com.yunmo.iot.domain.core.Project;
import com.yunmo.iot.domain.core.repository.DeviceRepository;
import com.yunmo.iot.domain.core.repository.ProjectRepository;
import com.yunmo.iot.domain.core.value.DeviceInstall;
import com.yunmo.iot.domain.core.value.DeviceModifyInstall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * @author lh
 */
@Service
public class ProjectDeviceInstallService{
    @Autowired
    DeviceRepository deviceRepository;
    @Autowired
    ProjectRepository projectRepository;

    public void checkWithProject(Device device, Tenant tenant) {
        Asserts.found(device);
        if (device.getProjectId() != null) {
            Asserts.present(projectRepository.findById(device.getProjectId()),
                    p->p.getTenantId().equals(tenant.getDomain())||p.getOperatorId().equals(tenant.getDomain()));
        }

    }

    public Device installDevice(Tenant tenant, Project project, Device device, DeviceInstall deviceInstall) {
        checkWithProject(device, tenant);

        Problems.ensure(deviceInstall.getInstallLocation() != null, "设备安装位置不能为空");
        //重复安装检查
        if (device.getProjectId() != null) {
            Problems.ensure(device.getProjectId() == null, "设备已安装");
        }

        if (deviceInstall.getInstallTime() == null) {
            deviceInstall.setInstallTime(Instant.now());
        }
        return deviceRepository.save(deviceInstall.assignTo(device).setProjectId(project.getId()));
    }

    public Device uninstallDevice(Tenant tenant, Device device) {
        checkWithProject(device, tenant);
        Problems.ensure(device.getProjectId() != null, "设备已拆除");

        device = deviceRepository.save(device.setProjectId(null).setInstallTime(null).setInstallLocation(null));
        return device;
    }

    public Device modifyInstallDevice(Tenant tenant, Device device, DeviceModifyInstall deviceModifyInstall) {

        checkWithProject(device, tenant);

        Problems.ensure(device.getProjectId() != null, "设备未安装");
        Problems.ensure(deviceModifyInstall.getInstallTime() != null, "设备安装时间不能为空");
        Problems.ensure(deviceModifyInstall.getInstallLocation() != null, "设备安装位置不能为空");

        return deviceRepository.save(deviceModifyInstall.assignTo(device));
    }
}
