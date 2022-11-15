package com.yunmo.iot.repository.jdbi.values;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectOverviewStats {
    private long deviceCount;
    private long alertCountToday;
    private long commandCountToday;
}
