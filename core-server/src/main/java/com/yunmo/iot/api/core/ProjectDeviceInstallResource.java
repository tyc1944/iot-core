package com.yunmo.iot.api.core;

import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Problems;
import com.yunmo.domain.common.Tenant;
import com.yunmo.iot.domain.core.Device;
import com.yunmo.iot.domain.core.Project;
import com.yunmo.iot.domain.core.repository.DeviceRepository;
import com.yunmo.iot.domain.core.service.ProjectDeviceInstallService;
import com.yunmo.iot.domain.core.value.DeviceInstall;
import com.yunmo.iot.domain.core.value.DeviceModifyInstall;
import com.yunmo.iot.domain.event.DeviceChangedType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

/**
 * @author lh
 */
@Deprecated
@Transactional
@RestController
@RequestMapping("/api/projects/{projectId}/devices")
public class ProjectDeviceInstallResource extends DeviceResourceBase {

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    ProjectDeviceInstallService projectDeviceInstallService;

    @PostMapping("/{deviceId}")
    public void installDevice(@Principal Tenant tenant, @PathVariable("projectId") Project project,
                              @PathVariable("deviceId") Device device, @RequestBody DeviceInstall deviceInstall) {
        device =projectDeviceInstallService.installDevice(tenant, project, device, deviceInstall);

        sendMessage(tenant, device, DeviceChangedType.INSTALL, project.getId());
    }

    @DeleteMapping("/{deviceId}")
    public void uninstallDevice(@Principal Tenant tenant, @PathVariable("deviceId") Device device) {
        Long operateProjectId = device.getProjectId();
        device =  projectDeviceInstallService.uninstallDevice(tenant, device);

        sendMessage(tenant, device, DeviceChangedType.UNINSTALL, operateProjectId);
    }

    //修改安装信息
    @PutMapping("/{deviceId}")
    public void modifyInstallDevice(@Principal Tenant tenant, @PathVariable("deviceId") Device device, @RequestBody DeviceModifyInstall deviceModifyInstall) {
        device = projectDeviceInstallService.modifyInstallDevice(tenant, device, deviceModifyInstall);

        sendMessage(tenant, device, DeviceChangedType.MODIFY, device.getProjectId());
    }


}
