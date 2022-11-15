package com.yunmo.iot.domain.assets;

import com.yunmo.domain.common.Audited;
import com.yunmo.generator.annotation.AutoValueDTO;
import com.yunmo.generator.annotation.ValueField;
import io.genrpc.annotation.ProtoMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Map;

@Entity
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AutoValueDTO
@ProtoMessage
@Table(indexes={@Index(columnList="parentId",name="asset_parent_idx"),
        @Index(columnList="projectId, assetType",name="asset_project_type_idx")})
public class AssetEntity extends Audited {

    @Id
    @GenericGenerator(name = "sequence_id", strategy = "com.yunmo.id.HibernateIdentifierGenerator")
    @GeneratedValue(generator = "sequence_id")
    private Long id;

    private Long projectId;

    private String path;

    @ValueField("Search")
    private Long parentId;


    @ValueField("Search")
    private String name;

    @ValueField("Search")
    private AssetType assetType;

    @ValueField
    @Type( type = "json" )
    @Column(columnDefinition = "json")
    private Map<String,Object> attributes;

    @ValueField
    private Long deviceId;

    @ValueField
    private String description;
}
