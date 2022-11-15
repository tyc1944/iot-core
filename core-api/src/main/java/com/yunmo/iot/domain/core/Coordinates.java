package com.yunmo.iot.domain.core;

import io.genrpc.annotation.ProtoMessage;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ProtoMessage
public class Coordinates {
    private String longitude;
    private String latitude;
}
