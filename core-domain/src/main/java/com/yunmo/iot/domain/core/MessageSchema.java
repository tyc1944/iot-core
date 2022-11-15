package com.yunmo.iot.domain.core;

import com.yunmo.domain.common.Audited;
import com.yunmo.generator.annotation.AutoValueDTO;
import com.yunmo.generator.annotation.ValueField;
import com.yunmo.iot.schema.RecordFormat;
import com.yunmo.iot.schema.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;

@Data
@Entity
@AutoValueDTO
@Table(indexes={@Index(columnList="productId,channel,overrideTime",name="product_channel_unique_idx", unique = true)})
@SQLDelete(sql = "update message_schema set deleted = 1, override_time = now() where id = ?")
@Where(clause = "deleted = 0")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class MessageSchema extends Audited implements Serializable {
    @Id
    @GenericGenerator(name = "sequence_id", strategy = "com.yunmo.id.HibernateIdentifierGenerator")
    @GeneratedValue(generator = "sequence_id")
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String channel = "default";

    private Instant overrideTime;

    @NotNull
    @ValueField
    @Column(nullable = false)
    private RecordFormat recordFormat;

    @NotNull
    @Type( type = "json" )
    @Column(columnDefinition = "json", nullable = false)
    @ValueField
    private Schema recordSchema;

    private boolean deleted;

}
