package com.yunmo.iot.domain.rule;

import io.genrpc.annotation.ProtoEnum;

import java.nio.charset.StandardCharsets;
@ProtoEnum
public enum GeoFenceDetect {
    inside,// is when an object is inside the specified area.
    outside,//  is when an object is outside the specified area.
    enter,//  is when an object that was not previously in the fence has entered the area.
    exit,//  is when an object that was previously in the fence has exited the area.
    cross; //is when an object that was not previously in the fence has entered and exited the area.

    public final byte[] bytes;

    GeoFenceDetect() {
        bytes = name().getBytes(StandardCharsets.US_ASCII);
    }

    public byte[] getBytes() {
        return bytes;
    }
}
