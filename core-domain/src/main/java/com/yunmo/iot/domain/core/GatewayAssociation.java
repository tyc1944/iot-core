package com.yunmo.iot.domain.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Data
@Entity
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
@IdClass(GatewayAssociation.ID.class)
//@Table(uniqueConstraints={@UniqueConstraint(name="DEVICE_ATTACH_UNIQUE_IDX", columnNames="device_id")})
public class GatewayAssociation {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static final class ID implements Serializable {
        private static final long serialVersionUID = 1772365931059197056L;
        private Long deviceId;
        private Long gatewayId;
    }

    @Id
    @Column(name = "device_id")
    private Long deviceId;

    @Id
    @Column(name = "gateway_id")
    private Long gatewayId;

    @CreatedBy
    private Long createdBy;

    @CreatedDate
    private Instant createdTime;
}
