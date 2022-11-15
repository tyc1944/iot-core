package com.yunmo.iot.domain.app;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yunmo.domain.common.Audited;
import com.yunmo.generator.annotation.AutoValueDTO;
import com.yunmo.generator.annotation.ValueField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Arrays;

@Entity
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AutoValueDTO
public class AppProfile extends Audited {
    @Id
    @GenericGenerator(name = "sequence_id", strategy = "com.yunmo.id.HibernateIdentifierGenerator")
    @GeneratedValue(generator = "sequence_id")
    private Long id;

    @ValueField
    private String name;


    @JsonIgnore
    private String token;

    @ValueField
    private AppStatus status;

    @ValueField
    private AppType appType;

    private String appKey;

    private Long tenantId;

    @Type( type = "json" )
    @Column(columnDefinition = "json")
    @ValueField
    private Long[] approvedProjectIds;

    @Type( type = "json" )
    @Column(columnDefinition = "json")
    @ValueField
    private Long[] approvedDeviceIds;

    public void resetToken() {
        this.token = RandomStringUtils.randomAlphanumeric(128);
    }

    public boolean inApprovedProjects(Long projectId) {
        return approvedProjectIds != null && Arrays.stream(approvedProjectIds).anyMatch(projectId::equals);
    }

    public boolean inApprovedDevices(Long deviceId) {
        return approvedDeviceIds != null && Arrays.stream(approvedDeviceIds).anyMatch(deviceId::equals);
    }
}
