package com.yunmo.iot.domain.core;

import com.yunmo.domain.common.Audited;
import com.yunmo.domain.common.Events;
import com.yunmo.generator.annotation.AutoValueDTO;
import com.yunmo.generator.annotation.ValueField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PostPersist;
import java.io.Serializable;

@Data
@Entity
@AutoValueDTO
@EqualsAndHashCode(callSuper = true)
public class Product extends Audited implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "sequence_id", strategy = "com.yunmo.id.HibernateIdentifierGenerator")
    @GeneratedValue(generator = "sequence_id")
    private Long id;

    @ValueField
    private String name;

    @ValueField
    private String model;

    @ValueField
    private String manufacturer;

    @ValueField
    private String category;

    private Long tenantId;

    @PostPersist
    private void created() {
        Events.post(new ProductCreatedEvent(this));
    }

}
