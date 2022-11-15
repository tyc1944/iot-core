package com.yunmo.iot.repository.jdbi.values;

import lombok.Data;

import java.time.Instant;

@Data
public class OnlineOfflineStats {
    private Instant intervalTime;
    private long online;
    private long offline;
}
