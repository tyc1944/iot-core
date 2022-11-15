package com.yunmo.iot.domain.rule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yunmo.domain.common.Audited;
import com.yunmo.generator.annotation.AutoValueDTO;
import com.yunmo.generator.annotation.ValueField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Map;


@Data
@Entity
@AutoValueDTO
@EqualsAndHashCode(callSuper = true)
@Table(indexes={@Index(name="RULE_SPEC_PROJECT_PRODUCT_IDX", columnList="projectId, productId"),
@Index(name = "RULE_SPEC_NAME_IDX", columnList = "name"),
@Index(name = "RULE_SPEC_DEVICE_IDX", columnList = "deviceId")})
public class RuleSpec  extends Audited {
    private static final String UPDATE = "Update";
    private static final String SEARCH = "Search";

    @Id
    @GenericGenerator(name = "sequence_id", strategy = "com.yunmo.id.HibernateIdentifierGenerator")
    @GeneratedValue(generator = "sequence_id")
    private Long id;

    @ValueField({SEARCH,UPDATE})
    private String name;

    @ValueField({SEARCH,UPDATE})
    private Long deviceId;

    @ValueField({UPDATE})
    private Boolean enabled;

    private Long tenantId;

    private Long projectId;

    @ValueField({SEARCH})
    private Long schemaId;

    @ValueField({UPDATE})
    private CommandTriggerStrategy triggerStrategy;

    @ValueField({UPDATE})
    private String expression;

    @ValueField({UPDATE})
    private ActionType action;

    @ValueField({UPDATE})
    private Long commandTemplateId;

    @ValueField({UPDATE})
    private Long commandTargetId;

    @ValueField({UPDATE})
    private String alertType;

    @ValueField({UPDATE})
    private AlertPrincipal principal;

    @ValueField({UPDATE})
    private String notification;

    private Long productId;

    //以下为冗余字段或生成字段
    @JsonIgnore
    private String telemetryChannel;

    @JsonIgnore
    private GeneratedClassCode generated;

    @JsonIgnore
    private String commandChannel;

    @JsonIgnore
    private String commandEncoded;

    @ValueField({UPDATE})
    @Type( type = "json" )
    @Column(columnDefinition = "json")
    private Map<String,Object> variables;
}
