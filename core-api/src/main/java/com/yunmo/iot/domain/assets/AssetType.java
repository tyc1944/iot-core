package com.yunmo.iot.domain.assets;

import com.yunmo.generator.annotation.JpaConverter;

@JpaConverter
public enum AssetType {
    PARK, //停车场n
    ENTERPRISE_ZONE, //企业园区
    RESIDENTIAL_COMPLEX, //居住小区
    FACTORY, //工厂
    BUILDING, //建筑
    ROOM, //房间
    DEVICE, //设备
    PLANT,//场地
    FARM,//农田
    EQUIPMENT, //装置
    INSTRUMENT, //仪器
    CAMERA, //摄像头
    SENSOR, //传感器
    ACTUATOR, //制动器
    VEHICLE, //车
    DTU, //DTU

    METER, //计量表
    COLLECTOR, //采集器
    CONCENTRATOR, //集中器
}
