package com.yunmo.iot.domain.analysis;

import com.yunmo.generator.annotation.AutoValueDTO;
import com.yunmo.generator.annotation.ValueField;
import com.yunmo.iot.schema.Schema;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
@AutoValueDTO
@SQLDelete(sql = "update analysis_table set deleted = 1 where id = ?")
@Where(clause = "deleted = 0")
@EntityListeners(AuditingEntityListener.class)
public class AnalysisTable {
    @Id
    @GenericGenerator(name = "sequence_id", strategy = "com.yunmo.id.HibernateIdentifierGenerator")
    @GeneratedValue(generator = "sequence_id")
    private Long id;

    @ValueField
    private String name;

    private String catalog;

    private String tableName;

    @Column(nullable = false)
    private Long productId;

    @ValueField
    @Column(nullable = false)
    private String channel = "default";

    @Type( type = "json" )
    @Column(columnDefinition = "json", nullable = false)
    @ValueField
    private Schema tableSchema;

    private boolean deleted;

    @CreatedBy
    protected Long createdBy;

    @CreatedDate
    protected Instant createdDate;
}
