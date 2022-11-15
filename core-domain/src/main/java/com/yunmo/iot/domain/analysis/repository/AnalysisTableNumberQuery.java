package com.yunmo.iot.domain.analysis.repository;

import com.yunmo.iot.domain.analysis.AnalysisTable;
import com.yunmo.iot.domain.analysis.value.AggregateNumber;
import com.yunmo.iot.domain.analysis.value.DeviceRanking;
import com.yunmo.iot.domain.analysis.value.HourlyNumber;
import com.yunmo.iot.domain.analysis.value.ProjectRanking;

import java.time.LocalDate;
import java.util.List;

public interface AnalysisTableNumberQuery {
    List<HourlyNumber> selectHourly(long[] projectIds, long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to);
    List<AggregateNumber> selectDaily(long[] projectIds, long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to);
    List<AggregateNumber> selectWeekly(long[] projectIds,  long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to);
    List<AggregateNumber> selectMonthly(long[] projectIds, long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to);
    List<AggregateNumber> selectQuarterly(long[] projectIds, long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to);
    List<AggregateNumber> selectYearly(long[] projectIds, long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to);

    List<ProjectRanking> rankingProjectByDay(long[] projectIds, long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to);
    List<ProjectRanking> rankingProjectByMonth(long[] projectIds, long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to);
    List<ProjectRanking> rankingProjectByYear(long[] projectIds, long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to);

    List<DeviceRanking> rankingDeviceByDay(long[] projectIds, long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to);
    List<DeviceRanking> rankingDeviceByMonth(long[] projectIds, long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to);
    List<DeviceRanking> rankingDeviceByYear(long[] projectIds, long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to);
}
