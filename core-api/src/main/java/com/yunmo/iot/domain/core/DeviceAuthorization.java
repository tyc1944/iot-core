package com.yunmo.iot.domain.core;

import io.genrpc.annotation.ProtoMessage;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@ProtoMessage
@Accessors(chain = true)
public class DeviceAuthorization {
    private long cloudId;
    private String localId;
    private String patch;
    private String username;
    private String password;
    private String clientId;

}
