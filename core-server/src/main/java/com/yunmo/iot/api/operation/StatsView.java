package com.yunmo.iot.api.operation;

import com.yunmo.iot.domain.core.DeviceConnectionStatus;
import com.yunmo.iot.repository.jdbi.values.AlertCountStats;
import com.yunmo.iot.repository.jdbi.values.ConnectionStatusCount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatsView {
    public static StatsView alertCount(List<AlertCountStats> stats) {
        return new StatsView(stats.stream().mapToLong(s->s.getCount()).sum(),
                stats.stream().collect(Collectors.toMap(s->s.getType(), s->s.getCount())));
    }

    public static StatsView statusCount(List<ConnectionStatusCount> stats) {
        var groups = stats.stream().collect(Collectors.toMap(s->s.getStatus().name(), s->s.getCount()));
        for(var status : DeviceConnectionStatus.values()) {
            if (!groups.containsKey(status.name())){
                groups.put(status.name(),0l);
            }
        }
        return new StatsView(stats.stream().mapToLong(s->s.getCount()).sum(), groups);
    }

    private long total;

    private Map<String,Long> stats;
}
