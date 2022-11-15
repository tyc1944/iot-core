package com.yunmo.iot.api.core;

import com.yunmo.iot.domain.core.value.DeviceBatch;
import io.genrpc.annotation.ProtoMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@ProtoMessage
@Getter
@Setter
public class BatchByCount extends DeviceBatch {
    private int count;
}

