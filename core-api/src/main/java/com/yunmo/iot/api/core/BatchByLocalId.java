package com.yunmo.iot.api.core;

import com.yunmo.iot.domain.core.value.DeviceBatch;
import io.genrpc.annotation.ProtoMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@ProtoMessage
@Getter
@Setter
@Accessors(chain = true)
public class BatchByLocalId extends DeviceBatch {
    private List<String> localIds;
}

