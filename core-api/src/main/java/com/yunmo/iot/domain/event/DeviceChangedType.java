package com.yunmo.iot.domain.event;

import com.yunmo.generator.annotation.JpaConverter;

@JpaConverter
public enum DeviceChangedType {
  ADD,
  INSTALL,
  MODIFY,
  UNINSTALL,
}
