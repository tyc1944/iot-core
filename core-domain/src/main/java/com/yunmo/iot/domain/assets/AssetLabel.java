package com.yunmo.iot.domain.assets;

import com.yunmo.generator.annotation.AutoValueDTO;
import com.yunmo.generator.annotation.ValueField;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

@Entity
@Data
@Accessors(chain = true)
@IdClass(AssetLabel.class)
@AutoValueDTO
public class AssetLabel implements Serializable {

    @Id
    private Long projectId;

    @Id
    @ValueField
    private String name;

    @Id
    private Long assetId;
}
