package com.yunmo.iot.domain.analysis.value;

import lombok.Data;

import java.util.Date;

@Data
public class AggregateNumber {
    private Date __date;
    private Double value;
}
