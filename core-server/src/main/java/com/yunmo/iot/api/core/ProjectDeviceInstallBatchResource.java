package com.yunmo.iot.api.core;

import com.yunmo.domain.common.Asserts;
import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.iot.domain.core.Device;
import com.yunmo.iot.domain.core.Project;
import com.yunmo.iot.domain.core.repository.DeviceRepository;
import com.yunmo.iot.domain.core.service.ProjectDeviceInstallService;
import com.yunmo.iot.domain.event.DeviceChangedType;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.*;

/**
 * @author lh
 */
@Deprecated
@Transactional
@RestController
@RequestMapping("/api/projects/{projectId}/devices")
public class ProjectDeviceInstallBatchResource extends DeviceResourceBase {

    @Autowired
    ProjectDeviceInstallService projectDeviceInstallService;
    @Autowired
    DeviceRepository deviceRepository;

    @PostMapping("/batch")
    public void installDeviceById(@Principal Tenant tenant, @PathVariable("projectId") Project project,
                             @RequestBody Set<BatchDeviceInstallById> batchDeviceInstallByIdes) {

        List<Device> batchInstallDevices = new ArrayList<>();
        for (BatchDeviceInstallById batchDeviceInstallByIde : batchDeviceInstallByIdes) {
            Optional<Device> device = deviceRepository.findById(batchDeviceInstallByIde.getId());
            Asserts.present(device, item -> true);

            Device installDevice = projectDeviceInstallService.installDevice(tenant, project, device.get(), batchDeviceInstallByIde);
            batchInstallDevices.add(installDevice);
        }

        batchInstallDevices.forEach(device -> {
            sendMessage(tenant, device, DeviceChangedType.INSTALL, project.getId());
        });

    }

    @DeleteMapping("/batch")
    public void uninstallDevice(@Principal Tenant tenant, @RequestBody Set<Long> deviceIds) {
        List<Map<Long, Device>> uninstallProjectIdAndDeviceMapList = new ArrayList<>();

        for (Long deviceId : deviceIds) {
            Optional<Device> device = deviceRepository.findById(deviceId);
            Asserts.present(device, item -> true);
            Device uninstallDevice = device.get();
            Long projectId = uninstallDevice.getProjectId();
            uninstallDevice = projectDeviceInstallService.uninstallDevice(tenant, uninstallDevice);

            uninstallProjectIdAndDeviceMapList.add(Map.of(projectId, uninstallDevice));
        }

        uninstallProjectIdAndDeviceMapList.forEach(operateProjectIdDeviceMap -> operateProjectIdDeviceMap.forEach((operateProjectId, device) -> {
            sendMessage(tenant, device, DeviceChangedType.UNINSTALL, operateProjectId);
        }));
    }

    //修改安装信息
    @PutMapping("/batch/install/change")
    public void modifyInstallDevice(@Principal Tenant tenant,
                                    @PathVariable("projectId") Project project,
                                    @RequestBody BatchDeviceInstallChange deviceInstallChange) {
        if (CollectionUtils.isNotEmpty(deviceInstallChange.getBatchDeviceInstallByIds())) {
            installDeviceById(tenant,project,deviceInstallChange.getBatchDeviceInstallByIds());
        }

        if (CollectionUtils.isNotEmpty(deviceInstallChange.getUnInstallDeviceIds())) {
            uninstallDevice(tenant, deviceInstallChange.getUnInstallDeviceIds());
        }
    }

}
