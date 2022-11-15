package com.yunmo.iot.api.core;

import com.yunmo.iot.domain.core.value.DeviceBatch;
import io.genrpc.annotation.ProtoMessage;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@ProtoMessage
@Data
@Accessors(chain = true)
public class BatchById {
    private List<Long> ids;
}

