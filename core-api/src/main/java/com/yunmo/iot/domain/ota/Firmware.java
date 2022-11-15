package com.yunmo.iot.domain.ota;

import com.yunmo.generator.annotation.ValueField;
import io.genrpc.annotation.ProtoMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@ProtoMessage
@Builder
public class Firmware {

    @ValueField
    private String name;

    @ValueField
    private String description;

    @ValueField
    private String version;

    @ValueField
    private String url;

    private String md5;

}
