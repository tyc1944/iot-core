package com.yunmo.iot.domain.rule;

import com.yunmo.generator.annotation.AutoValueDTO;
import com.yunmo.generator.annotation.ValueField;
import io.genrpc.annotation.ProtoMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(indexes={@Index(name="RULE_ALERT_TYPE_TIME_IDX", columnList="audienceTenantId, alertType, alertTime"),
        @Index(name="RULE_ALERT_PROJECT_DEVICE_IDX", columnList="projectId,deviceId"),
        @Index(name="RULE_ALERT_PRODUCT_IDX", columnList="productId"),
        @Index(name="RULE_ALERT_STATUS_IDX", columnList="alertStatus")})
@ProtoMessage
@AutoValueDTO
@Builder
public class RuleAlert {
    @Id
    @GenericGenerator(name = "sequence_id", strategy = "com.yunmo.id.HibernateIdentifierGenerator")
    @GeneratedValue(generator = "sequence_id")
    private Long id;

    private Long projectId;

    private Long productId;

    @ValueField("Search")
    private Long deviceId;

    @ValueField("Search")
    private String alertType;

    @Transient
    private AlertPrincipal principal;

    @ValueField("Search")
    private AlertStatus alertStatus;

    private Instant alertTime;

    private String notification;

    private Long audienceTenantId;

    @Type( type = "json" )
    @Column(columnDefinition = "json")
    private String message;

}
