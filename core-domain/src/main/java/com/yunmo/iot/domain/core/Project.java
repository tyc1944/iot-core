package com.yunmo.iot.domain.core;

import com.yunmo.domain.common.Audited;
import com.yunmo.generator.annotation.AutoValueDTO;
import com.yunmo.generator.annotation.ValueField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@AutoValueDTO
@EqualsAndHashCode(callSuper = true)
@Table(indexes={@Index(name="PROJECT_TENANT_ID_IDX", columnList="tenantId")})
public class Project extends Audited {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "sequence_id", strategy = "com.yunmo.id.HibernateIdentifierGenerator")
    @GeneratedValue(generator = "sequence_id")
    private Long id;

    @ValueField
    private String name;

    @ValueField
    private IndustryType industry;

    @Embedded
    @ValueField
    private Location location;

    @ValueField
    private String remark;

    private Long tenantId;

    @ValueField
    private Long operatorId;
}
