package com.yunmo.iot.domain.rule;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.yunmo.domain.common.Audited;
import com.yunmo.generator.annotation.AutoValueDTO;
import com.yunmo.generator.annotation.ValueField;
import com.yunmo.iot.domain.core.StringFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Map;

@Data
@Entity
@AutoValueDTO
@EqualsAndHashCode(callSuper = true)
public class CommandTemplate extends Audited {
    @Id
    @GenericGenerator(name = "sequence_id", strategy = "com.yunmo.id.HibernateIdentifierGenerator")
    @GeneratedValue(generator = "sequence_id")
    private Long id;

    @ValueField
    private String name;

    private Long productId;

    @ValueField
    private Long schemaId; //对应修改时要做commandJson的编码检查，并更新响应信息

    @ValueField
    private String channel;

    @ValueField
    private StringFormat format;

    @Column(length = 2048)
    @ValueField
    private String command; //目前仅支持静态的，将来可能支持参数化的

    @Column(length = 2048)
    private String commandEncoded; //base64
}
