package com.yunmo.iot.domain.core;


import com.alibaba.excel.annotation.ExcelProperty;
import com.google.common.collect.Maps;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import com.yunmo.domain.common.Audited;
import com.yunmo.domain.common.Events;
import com.yunmo.generator.annotation.AutoValueDTO;
import com.yunmo.generator.annotation.ValueField;
import io.genrpc.annotation.ProtoMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.*;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@AutoValueDTO
@Accessors(chain = true)
@SQLDelete(sql = "update device set deleted = 1 where id = ?")
@Where(clause = "deleted = 0")
@ProtoMessage
@Table(indexes={@Index(name="DEVICE_LOCAL_ID_IDX", columnList="localId"),
        @Index(name="DEVICE_GATEWAY_IDX", columnList="gateway"),
        @Index(name="BATCH_IDX", columnList="batch"),
        @Index(name="DEVICE_PROJECT_PRODUCT_IDX", columnList="projectId,productId")})
public class Device extends Audited {

    @Id
    @GenericGenerator(name = "sequence_id", strategy = "com.yunmo.id.HibernateIdentifierGenerator")
    @GeneratedValue(generator = "sequence_id")
    @ValueField(value = "Brief",noDefault = true)
    private Long id;

    @ValueField({"Search","Brief","ChildValue"})
    @ExcelProperty("localId")
    private String localId;

    @ValueField({"Brief","Batch"})
    private Boolean gateway;

    @ValueField(value = {"Search","Batch"})
    @ExcelProperty("projectId")
    private Long projectId;

    @ValueField({"Search","ChildValue","Batch"})
    @ExcelProperty("productId")
    private Long productId;

    @ValueField({"Batch"})
    private Long hubId;

    @Type( type = "json" )
    @Column(columnDefinition = "json")
    private Credentials credentials;

    @ValueField
    @Type( type = "json" )
    @Column(columnDefinition = "json")
    private DeviceConfig config;

    @Type( type = "json" )
    @Column(columnDefinition = "json")
    private DeviceState state;

    @ValueField(value = "Search", noDefault = true)
    private DeviceConnectionStatus deviceStatus = DeviceConnectionStatus.INACTIVE;

    @ValueField(value = "Search", noDefault = true)
    private DeviceOperationStatus operationStatus = DeviceOperationStatus.NORMAL;

    @ValueField(value = {"Batch"}, noDefault = true)
    private String batch;


    @Deprecated//放到attributes里
    @ValueField(value = {"Install","ModifyInstall"})
    private String installLocation;


    @Deprecated//放到attributes里
    @ValueField(value = {"Install","ModifyInstall"})
    private Instant installTime;

    private Instant lastOnlineTime;

    private Instant lastOfflineTime;

    private boolean deleted;


    @Deprecated//放到attributes里
    @Type( type = "json" )
    @Column(columnDefinition = "json")
    private Coordinates coordinates;

    @ValueField
    @Type( type = "json" )
    @Column(columnDefinition = "json")
    private List<String> components;

    @ValueField
    @Type( type = "json" )
    @Column(columnDefinition = "json")
    private Map<String,Object> attributes;


    public void updateConfig(Map<String,Object> content) {
        int newVersion = Optional.ofNullable(config)
                .map(c->c.getVersion()+1).orElse(0);
        DeviceConfig newConfig = new DeviceConfig()
                .setTimestamp(Instant.now())
                .setContent(Maps.newHashMap(content))
                .setVersion(newVersion);

        Events.post(new DeviceConfigEvent()
                .setDevice(this)
                .setConfig(newConfig));
        this.config = newConfig;
    }

    @PreRemove
    private void remove() {
        Events.post(new DeviceRemoveEvent().setDevice(this));
        this.deleted = true;
    }

    @PrePersist
    private void setStateConfig() {
        if(this.state == null) {
            this.state = new DeviceState()
            .setVersion(0)
            .setTimestamp(Instant.now())
            .setResult(ConfigResultCode.OK)
            .setContent(Collections.emptyMap());
        }

        if(this.config == null) {
            this.config = new DeviceConfig()
                    .setVersion(0)
                    .setTimestamp(Instant.now())
                    .setContent(Collections.emptyMap());
        }
    }
}
