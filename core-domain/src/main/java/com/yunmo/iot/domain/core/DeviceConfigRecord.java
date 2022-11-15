package com.yunmo.iot.domain.core;

import com.yunmo.domain.common.Audited;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;
import java.util.Map;

@Entity
@Data
@Accessors(chain = true)
@IdClass(DeviceConfigRecord.ID.class)
@EqualsAndHashCode(callSuper = true)
public class DeviceConfigRecord extends Audited {
    @Data
    public static final class ID implements Serializable {
        private static final long serialVersionUID = 624799569454636780L;
        private Long deviceId;
        private int version;
    }

    @Id
    @Column(name = "device_id")
    private Long deviceId;

    @Id
    @Column(name = "version")
    private int version;

    private ConfigResultCode status;

    @Type( type = "json" )
    @Column(columnDefinition = "json")
    private Map<String,Object> content;
}
