package com.yunmo.iot.domain.analysis.value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AggregateCount {
    private Date __date;

    private Object key;

    private BigInteger[] count;
}
