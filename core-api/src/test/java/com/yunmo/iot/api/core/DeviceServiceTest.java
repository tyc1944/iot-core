package com.yunmo.iot.api.core;

import com.google.common.collect.Lists;
import com.yunmo.iot.domain.core.Device;
import com.yunmo.iot.domain.core.EntityEventRecordRpc;
import com.yunmo.iot.domain.core.GenericTelemetryRecordRpc;
import com.yunmo.iot.domain.core.value.DeviceValue;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnit4.class)
class DeviceServiceTest {
    DeviceService deviceService;
    ManagedChannel channel;
    TelemetryRecordService telemetryRecordService;
    EntityEventService entityEventService;

    @BeforeClass
    public void client() {

    }

    //@Test
    void getDeviceById(){
        channel = ManagedChannelBuilder.forAddress("192.168.102.125", 9090)
                .usePlaintext()
                .build();
        deviceService = DeviceServiceGrpc.newBlockingStub(channel);
        Device device = deviceService.getDeviceById(246769716830404612l);
        System.out.println(device.getHubId());
    }

//    @Test
    void listEntityEventRecord(){
        channel = ManagedChannelBuilder.forAddress("192.168.102.125", 9090)
                .usePlaintext()
                .build();
        entityEventService = EntityEventServiceGrpc.newBlockingStub(channel);
        List<EntityEventRecordRpc> list = entityEventService.queryTelemetryByDay("staff_C093","kexing_video_recorder",1648396800);
        System.out.println(list);
    }

//    @Test
    void listTelemetryRecord(){
        channel = ManagedChannelBuilder.forAddress("192.168.102.125", 9090)
                .usePlaintext()
                .build();
        telemetryRecordService = TelemetryRecordServiceGrpc.newBlockingStub(channel);
        List<GenericTelemetryRecordRpc> telemetryRecords = telemetryRecordService.listRance(306170282765189122l,
                Instant.now().minus(30, ChronoUnit.DAYS),Instant.now(),"default");
        System.out.println(telemetryRecords.toArray());
    }


//    @Test
    void create() {
        channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();
        deviceService = DeviceServiceGrpc.newBlockingStub(channel);

        Device device = deviceService.create(new DeviceValue().setGateway(false)
                .setLocalId("test").setProductId(164205189694226433l).setHubId(83631878082396161l)
                .setComponents(Lists.newArrayList()));
        System.out.println(device);
    }
}