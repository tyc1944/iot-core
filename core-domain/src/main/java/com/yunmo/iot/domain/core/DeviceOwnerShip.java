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
@IdClass(DeviceOwnerShip.ID.class)
@EntityListeners(AuditingEntityListener.class)
@Accessors(chain = true)
public class DeviceOwnerShip {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static final class  ID implements Serializable {

        private static final long serialVersionUID = 7078172179299645968L;
        private Long deviceId;
        private Long ownerTenantId;
    }

    @Id
    @Column(name = "device_id")
    private Long deviceId;

    @Id
    @Column(name ="owner_tenant_id")
    private Long ownerTenantId;


    @CreatedBy
    private Long createdBy;

    @CreatedDate
    private Instant createdTime;
}
