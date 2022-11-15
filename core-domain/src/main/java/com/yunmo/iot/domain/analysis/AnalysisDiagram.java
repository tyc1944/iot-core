package com.yunmo.iot.domain.analysis;

import com.yunmo.domain.common.Audited;
import com.yunmo.generator.annotation.AutoValueDTO;
import com.yunmo.generator.annotation.ValueField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AutoValueDTO
public class AnalysisDiagram extends Audited {
    @Id
    @GenericGenerator(name = "sequence_id", strategy = "com.yunmo.id.HibernateIdentifierGenerator")
    @GeneratedValue(generator = "sequence_id")
    private Long id;

    @ValueField
    private String name;

    private Long tenantId;

    @Type( type = "json" )
    @Column(columnDefinition = "json")
    @ValueField
    private Long[] deviceIds;

    @ValueField
    private Long statisticsId;

    @ValueField
    private String columnName;

    @Type( type = "json" )
    @Column(columnDefinition = "json")
    @ValueField
    private int[] indexes;

    @ValueField
    private AggregatePeriod period;

}
