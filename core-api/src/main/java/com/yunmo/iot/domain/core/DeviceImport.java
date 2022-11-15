package com.yunmo.iot.domain.core;

import com.alibaba.excel.annotation.ExcelProperty;
import com.yunmo.iot.domain.core.Device;
import io.genrpc.annotation.ProtoMessage;
import java.lang.Long;
import java.lang.String;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

@Data
@EqualsAndHashCode
public class DeviceImport {
  @ExcelProperty(index = 1)
  private String localId;

  @ExcelProperty(index = 0)
  private Long productId;

  public Device assignTo(Device entity) {
    if(localId == null || localId.isEmpty() || productId == null){
      throw Problem.valueOf(Status.BAD_REQUEST,"产品ID和设备出厂编号不可以为空");
    }
    entity.setLocalId(this.getLocalId());
    entity.setProductId(this.getProductId());
    return entity;
  }

  public Device patchTo(Device entity) {
    if(this.getLocalId() != null) { entity.setLocalId(this.getLocalId()); };
    if(this.getProductId() != null) { entity.setProductId(this.getProductId()); };
    return entity;
  }

  public Device create() {
    Device entity = new Device();;
    assignTo(entity);
    return entity;
  }

  public static DeviceImport from(Device entity) {
    DeviceImport dto = new DeviceImport();
    dto.setLocalId(entity.getLocalId());
    dto.setProductId(entity.getProductId());
    return dto;
  }
}
