package com.yunmo.iot.api.core;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.yunmo.domain.common.Asserts;
import com.yunmo.domain.common.Events;
import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.iot.domain.core.*;
import com.yunmo.iot.domain.core.repository.DeviceHubRepository;
import com.yunmo.iot.domain.core.repository.DeviceRepository;
import com.yunmo.iot.domain.core.value.DeviceCommandRecordValue;
import com.yunmo.iot.domain.event.DeviceChangedType;
import com.yunmo.iot.repository.jpa.DeviceCommandRecordJpaRepository;
import com.yunmo.iot.repository.jpa.HubJpaRepository;
import com.yunmo.iot.repository.jpa.ProjectJpaRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Transactional
@RestController
@RequestMapping("/api/devices")
@Api(tags = "设备批量操作接口")
@Slf4j
public class DeviceBatchResource extends DeviceResourceBase implements DeviceBatchService {
    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    DeviceCommandRecordJpaRepository deviceCommandRecordRepository;

    @Autowired
    DeviceHubRepository deviceHubRepository;

    @Autowired
    HubJpaRepository hubRepository;

    @Autowired
    ProjectJpaRepository projectRepository;


    @PostMapping("/batch")
    @Override
    public List<DeviceAuthorization> createByLocalId(@RequestBody BatchByLocalId batch, @Principal Tenant tenant) {
        Asserts.present(projectRepository.findById(batch.getProjectId()),
                p -> p.getTenantId().equals(tenant.getDomain()));
        DeviceHub hub = hubRepository.getOne(batch.getHubId());

        List<Device> devices = batch.getLocalIds().stream().map(localId ->
                batch.create()
                        .setDeviceStatus(DeviceConnectionStatus.INACTIVE)
                        .setLocalId(localId)
                        .setDeleted(false)

        ).collect(Collectors.toList());
        deviceRepository.saveAll(devices);
        return toAuthorization(devices, hub);
    }

    @DeleteMapping("/batch/delete")
    @ApiOperation("批量删除设备")
    @Secured("ROLE_SYSTEM_ADMIN")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public void batchDelete(@Principal Tenant tenant,@RequestBody BatchById batch){
        List<Device> devices = deviceRepository.findAllById(batch.getIds());
        deviceRepository.deleteAll(devices);
    }

    @PostMapping("/excel/import")
    @ApiOperation("通过excel导入设备")
    @Secured("ROLE_SYSTEM_ADMIN")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public Iterable<Device> batchImportFromExcel(@Principal Tenant tenant, MultipartFile file) {
        List<DeviceImport> list = new ArrayList<>();
        try {
            EasyExcel.read(file.getInputStream(), DeviceImport.class, new ExcelReadListener(list)).sheet().doRead();
        } catch (Exception e) {
            throw Problem.valueOf(Status.UNSUPPORTED_MEDIA_TYPE, "您上传的文件格式不支持");
        }
        List<DeviceImport> distinct = list.stream().distinct().collect(Collectors.toList());
        if(distinct.size() < list.size()){
            throw Problem.valueOf(Status.SERVICE_UNAVAILABLE,"您上传的文件中存在重复的设备出厂编号");
        }
        List<Device> needSave = new ArrayList<>();
        for (DeviceImport deviceImport : list) {
            Device device = deviceImport.create();
            DeviceHub deviceHub = hubRepository.findFirstByProductId(device.getProductId());
            Device anotherDevice = deviceJpaRepository.findByProductIdAndLocalId(device.getProductId(),device.getLocalId());
            if(anotherDevice != null){
                throw Problem.valueOf(Status.SERVICE_UNAVAILABLE,"设备出厂编号重复");
            }
            if(deviceImport.getLocalId().length()>50){
                throw Problem.valueOf(Status.BANDWIDTH_LIMIT_EXCEEDED,"设备出厂编号:"+deviceImport.getLocalId()+",超出50位的限制");
            }
            if(deviceHub != null){
                device.setHubId(deviceHub.getId());
            } else {
                throw Problem.valueOf(Status.NOT_FOUND,"产品:"+device.getProductId()+"不存在");
            }
            needSave.add(device);

        }
        Iterable<Device> result = deviceRepository.saveAll(needSave);
        result.forEach(device -> sendMessage(tenant, device, DeviceChangedType.ADD));
        return result;
    }

    private List<DeviceAuthorization> toAuthorization(List<Device> devices, DeviceHub hub) {
        return devices.stream().map(device ->
                new DeviceAuthorization().setPassword(CredentialsUtil.createJwt(hub, device))
                        .setCloudId(device.getId())
                        .setLocalId(device.getLocalId())
                        .setUsername(device.getId().toString())
                        .setPatch(device.getBatch())
                        .setClientId(String.format("projects/{}/hubs/{}/devices/{}",
                                device.getProjectId(), device.getHubId(), device.getId()))
        ).collect(Collectors.toList());
    }

    @PostMapping("/batch/count")
    @Override
    public List<DeviceAuthorization> createByCount(@RequestBody BatchByCount batch, @Principal Tenant tenant) {
        BatchByLocalId param = new BatchByLocalId()
                .setLocalIds(IntStream.range(0, batch.getCount())
                        .mapToObj(i -> (String) null).collect(Collectors.toList()));
        param.setBatch(batch.getBatch())
                .setProductId(batch.getProductId())
                .setProjectId(batch.getProjectId())
                .setGateway(batch.getGateway());
        return createByLocalId(param, tenant);
    }

    @GetMapping("/batch")
    @Override
    public List<DeviceAuthorization> getByBatch(String batch, long productId, long projectId, long hubId) {
        DeviceHub hub = hubRepository.getOne(hubId);
        return toAuthorization(deviceRepository.findByProductIdAndProjectIdAndBatch(
                productId, projectId, batch), hub);
    }

    @PostMapping("/batch/commands")
    public void batchCommands(@RequestBody DeviceBatchConfig batch, @Principal Tenant tenant) {
        DeviceCommandRecordValue value = new DeviceCommandRecordValue()
                .setChannel(batch.getCommand().getChannel())
                .setContent(batch.getCommand().getContent())
                .setFormat(StringFormat.valueOf(batch.getCommand().getFormat()));
        batch.getDeviceIds().forEach(d -> {
            Optional<Device> deviceOpt = deviceRepository.findById(d);
            deviceOpt.ifPresent(device -> {
                checkWithProject(device, tenant);
                DeviceCommandRecord record = value.create()
                        .setProjectId(device.getProjectId())
                        .setDeviceId(device.getId());
                deviceCommandRecordRepository.save(record);
                Events.post(record);
            });
        });
    }

}
