package com.yunmo.iot.domain.rule;

import com.yunmo.domain.common.Audited;
import com.yunmo.generator.annotation.AutoValueDTO;
import com.yunmo.generator.annotation.ValueField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Data
@Entity
@AutoValueDTO
@EqualsAndHashCode(callSuper = true)
public class GeoFence extends Audited {

    @Id
    @GenericGenerator(name = "sequence_id", strategy = "com.yunmo.id.HibernateIdentifierGenerator")
    @GeneratedValue(generator = "sequence_id")
    private Long id;

    @ValueField
    private String name;

    private Long projectId;

    private Long deviceId;

    private Long tenantId;

    @ValueField
    @Type( type = "json" )
    @Column(columnDefinition = "json")
    private String fenceGeoJson;

    @ValueField
    private Boolean enabled;

}
