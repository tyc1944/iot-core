package com.yunmo.iot.domain.analysis.value;

import lombok.*;

import java.math.BigInteger;
import java.sql.Array;
import java.util.Date;


@Data
@EqualsAndHashCode(callSuper = true)
public class HourlyCount extends AggregateCount {
    private int __hour;

    @Builder
    public HourlyCount(Date __date, Object key, BigInteger[] count, int __hour) {
        super(__date, key, count);
        this.__hour = __hour;
    }
}
