package com.yunmo.iot.domain.core;

import com.yunmo.domain.common.Audited;
import com.yunmo.generator.annotation.AutoValueDTO;
import com.yunmo.generator.annotation.ValueField;
import io.genrpc.annotation.ProtoMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Map;

/**
 * 设备集中接入点
 */
@Data
@Entity
@AutoValueDTO
@ProtoMessage
@EqualsAndHashCode(callSuper = true)
public class DeviceHub extends Audited {
    @Id
    @GenericGenerator(name = "sequence_id", strategy = "com.yunmo.id.HibernateIdentifierGenerator")
    @GeneratedValue(generator = "sequence_id")
    private Long id;

    @ValueField
    private String name;

    private Long productId;

    @Type( type = "json" )
    @Column(columnDefinition = "json")
    @ValueField
    private Credentials credentials;

    @ValueField
    private ProtocolType protocol;

    @Type( type = "json" )
    @Column(columnDefinition = "json")
    @ValueField
    private Map<String,Object> connectConfig;

    @ValueField
    private ProvisionStrategy provisionStrategy;
}
