package com.yunmo.iot.domain.analysis.value;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
public class ProjectRanking {
    private long __project_id;
    private String name;
    private Date __date;
    private Double value;
}
