package com.yunmo.iot.domain.ota;

import com.yunmo.domain.common.Audited;
import com.yunmo.generator.annotation.AutoValueDTO;
import io.genrpc.annotation.ProtoMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.Instant;

@Data
@ProtoMessage
@Entity(name="device_ota_record")
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
public class DeviceOTARecord  {
    @Id
    @GenericGenerator(name = "sequence_id", strategy = "com.yunmo.id.HibernateIdentifierGenerator")
    @GeneratedValue(generator = "sequence_id")
    private Long id;

    private Long deviceId;

    private Firmware firmware;

    @CreatedBy
    protected Long createdBy;

    @CreatedDate
    protected Instant createdDate;
}
