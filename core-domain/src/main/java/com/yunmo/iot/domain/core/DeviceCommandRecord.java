package com.yunmo.iot.domain.core;

import com.yunmo.generator.annotation.AutoValueDTO;
import com.yunmo.generator.annotation.ValueField;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
@AutoValueDTO
public class DeviceCommandRecord {
    @Id
    @GenericGenerator(name = "sequence_id", strategy = "com.yunmo.id.HibernateIdentifierGenerator")
    @GeneratedValue(generator = "sequence_id")
    private Long id;

    private Long deviceId;
    private Long projectId;

    @ValueField
    private String channel;

    @ValueField
    private StringFormat format;

    @Column(length = 2048)
    @ValueField
    private String content;

    @CreatedBy
    private Long createdBy;

    @CreatedDate
    private Instant createdTime;
}
