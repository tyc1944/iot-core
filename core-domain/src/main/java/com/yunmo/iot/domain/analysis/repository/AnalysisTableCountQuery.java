package com.yunmo.iot.domain.analysis.repository;

import com.yunmo.iot.domain.analysis.AnalysisTable;
import com.yunmo.iot.domain.analysis.value.AggregateCount;
import com.yunmo.iot.domain.analysis.value.HourlyCount;

import java.time.LocalDate;
import java.util.List;

public interface AnalysisTableCountQuery {
    List<HourlyCount> selectHourly(long[] projectIds,long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to);
    List<AggregateCount> selectDaily(long[] projectIds,long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to);
    List<AggregateCount> selectWeekly(long[] projectIds,long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to);
    List<AggregateCount> selectMonthly(long[] projectIds,long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to);
    List<AggregateCount> selectQuarterly(long[] projectIds,long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to);
    List<AggregateCount> selectYearly(long[] projectIds, long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to);
}
