package com.yunmo.iot.domain.rule;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(indexes={@Index(name="FILTER_RULE_PRODUCT_IDX", columnList="productId")})
public class FilterRule extends Audited {
    private static final String UPDATE = "Update";
    private static final String SEARCH = "Search";

    @Id
    @GenericGenerator(name = "sequence_id", strategy = "com.yunmo.id.HibernateIdentifierGenerator")
    @GeneratedValue(generator = "sequence_id")
    private Long id;

    @ValueField({SEARCH,UPDATE})
    private String name;

    @ValueField({UPDATE})
    private Boolean enabled;

    private Long tenantId;

    @ValueField({SEARCH})
    private Long schemaId;

    @ValueField({UPDATE})
    private String expression;

    private Long productId;

    //以下为冗余字段或生成字段
    @JsonIgnore
    private String telemetryChannel;

    @JsonIgnore
    private GeneratedClassCode generated;
}
