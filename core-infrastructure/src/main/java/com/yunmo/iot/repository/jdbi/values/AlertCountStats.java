package com.yunmo.iot.repository.jdbi.values;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlertCountStats {
    private String type;
    private long count;
}
