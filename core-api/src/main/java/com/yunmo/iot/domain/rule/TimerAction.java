package com.yunmo.iot.domain.rule;

import com.yunmo.domain.common.Audited;
import com.yunmo.generator.annotation.AutoValueDTO;
import com.yunmo.generator.annotation.ValueField;
import io.genrpc.annotation.ProtoMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.Instant;
import java.util.List;

@Data
@Entity
@AutoValueDTO
@ProtoMessage
@EqualsAndHashCode(callSuper=true)
@Table(indexes={@Index(name="TIMER_ACTION_TENANT_IDX", columnList="tenantId, createdDate"),
@Index(name = "TIMER_ACTION_TARGET_IDX", columnList = "targetDeviceId")})
public class TimerAction extends Audited {

    @Id
    @GenericGenerator(name = "sequence_id", strategy = "com.yunmo.id.HibernateIdentifierGenerator")
    @GeneratedValue(generator = "sequence_id")
    private Long id;

    @ValueField(value = "Search", noDefault = true)
    private Long tenantId;

    @ValueField
    private String name;

    @ValueField
    private Instant triggerTime;

    @ValueField
    private Integer timeZoneOffsetHours;

    @ValueField
    private ActionRepeat actionRepeat;

    @ValueField
    private Boolean enabled;

    private Long version;

    @ValueField
    @Type( type = "json" )
    @Column(columnDefinition = "json")
    private List<DayOfWeek> customDays;

    @ValueField("Search")
    private Long targetDeviceId;

    @ValueField
    private Long commandTemplateId;

    public void initVersion() {
        version = 0l;
    }

    public void refreshVersion() {
        version  = System.currentTimeMillis();
    }
}
