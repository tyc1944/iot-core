package com.yunmo.iot.repository.jdbi.values;

import com.alibaba.excel.annotation.ExcelProperty;
import com.yunmo.generator.annotation.ValueField;
import com.yunmo.iot.domain.core.*;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.jdbi.v3.core.enums.EnumByName;

import javax.persistence.Column;
import java.time.Instant;

@Data
public class DeviceSelectView {
    private Long id;

    private String localId;

    private Boolean gateway;

    private Long projectId;

    private Long productId;

    private Long hubId;

    private DeviceConnectionStatus deviceStatus ;

    private DeviceOperationStatus operationStatus ;

    private String installLocation;

    private Instant installTime;

    private Instant createdDate;
}
