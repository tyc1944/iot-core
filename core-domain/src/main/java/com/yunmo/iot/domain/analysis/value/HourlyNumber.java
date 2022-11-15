package com.yunmo.iot.domain.analysis.value;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class HourlyNumber extends AggregateNumber {
    private int __hour;
}
