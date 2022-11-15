package com.yunmo.iot.repository.jdbi;

import com.yunmo.domain.common.Problems;
import com.yunmo.iot.domain.analysis.AnalysisTable;
import com.yunmo.iot.domain.analysis.repository.AnalysisTableNumberQuery;
import com.yunmo.iot.domain.analysis.value.AggregateNumber;
import com.yunmo.iot.domain.analysis.value.DeviceRanking;
import com.yunmo.iot.domain.analysis.value.HourlyNumber;
import com.yunmo.iot.domain.analysis.value.ProjectRanking;
import com.yunmo.iot.pipe.clickhouse.QueryGenerator;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import scala.Option;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class AnalysisTableJDBINumberQuery implements AnalysisTableNumberQuery {
    @Autowired
    Jdbi clickHouseJDBI;

    @Override
    public List<HourlyNumber> selectHourly(long[] projectIds,long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to) {
        Option<String> query = QueryGenerator.selectHourly(table.getCatalog(), table.getTableName(), table.getTableSchema(), new QueryGenerator.Where(
                projectIds, deviceIds, from, to, column, index
        ));

        Problems.ensure(query.isDefined(), "列不存在！");
        return clickHouseJDBI.withHandle(handle ->
                handle.createQuery(query.get()).mapToBean(HourlyNumber.class).collect(Collectors.toList()));
    }

    @Override
    public List<AggregateNumber> selectDaily(long[] projectIds, long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to) {
        Option<String> query = QueryGenerator.selectDaily(table.getCatalog(), table.getTableName(), table.getTableSchema(), new QueryGenerator.Where(
                projectIds, deviceIds, from, to,column, index
        ));
        return query(query, from);
    }

@Override
    public List<AggregateNumber> selectWeekly(long[] projectIds, long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to) {
        from = from.with(DayOfWeek.MONDAY);
        to = to.with(DayOfWeek.SUNDAY);

        Option<String> query = QueryGenerator.selectWeekly(table.getCatalog(), table.getTableName(), table.getTableSchema(), new QueryGenerator.Where(
                projectIds, deviceIds, from, to, column, index
        ));

        return query(query, from);
    }

    private List<AggregateNumber> query(Option<String> query, LocalDate from) {
        Problems.ensure(query.isDefined(), "列不存在！");
        return clickHouseJDBI.withHandle(handle ->
                handle.createQuery(query.get()).mapToBean(AggregateNumber.class).collect(Collectors.toList()));
    }

    @Override
    public List<AggregateNumber> selectMonthly(long[] projectIds, long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to) {
        from = from.withDayOfMonth(1);
        to = to.withDayOfMonth(to.lengthOfMonth());

        Option<String> query = QueryGenerator.selectMonthly(table.getCatalog(), table.getTableName(), table.getTableSchema(), new QueryGenerator.Where(
                projectIds, deviceIds, from, to, column, index
        ));
        return query(query, from);
    }

    @Override
    public List<AggregateNumber> selectQuarterly(long[] projectIds, long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to) {
        from = LocalDate.of(from.getYear(), from.getMonth().firstMonthOfQuarter(), 1);
        Month toMonth = to.getMonth().firstMonthOfQuarter().plus(2);
        to = LocalDate.of(to.getYear(), toMonth, toMonth.length(to.isLeapYear()));

        Option<String> query = QueryGenerator.selectQuarterly(table.getCatalog(), table.getTableName(), table.getTableSchema(), new QueryGenerator.Where(
                projectIds, deviceIds, from, to,column, index
        ));
        return query(query, from);
    }

    @Override
    public List<AggregateNumber> selectYearly(long[] projectIds, long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to) {
        from = LocalDate.of(from.getYear(),1,1);
        to = LocalDate.of(to.getYear(),12,31);

        Option<String> query = QueryGenerator.selectYearly(table.getCatalog(), table.getTableName(), table.getTableSchema(), new QueryGenerator.Where(
                projectIds, deviceIds, from, to,column, index
        ));
        return query(query, from);
    }

    @Override
    public List<ProjectRanking> rankingProjectByDay(long[] projectIds, long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to) {
        Option<String> query = QueryGenerator.rankingProjectByDay(table.getCatalog(), table.getTableName(), table.getTableSchema(), new QueryGenerator.Where(
                projectIds, deviceIds, from, to,column, index
        ));
        return rankingProject(query, from);
    }

    @Override
    public List<ProjectRanking> rankingProjectByMonth(long[] projectIds, long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to) {
        from = from.withDayOfMonth(1);
        to = to.withDayOfMonth(to.lengthOfMonth());

        Option<String> query = QueryGenerator.rankingProjectByMonth(table.getCatalog(), table.getTableName(), table.getTableSchema(), new QueryGenerator.Where(
                projectIds, deviceIds, from, to, column, index
        ));
        return rankingProject(query, from);
    }

    @Override
    public List<ProjectRanking> rankingProjectByYear(long[] projectIds, long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to) {
        from = LocalDate.of(from.getYear(),1,1);
        to = LocalDate.of(to.getYear(),12,31);

        Option<String> query = QueryGenerator.rankingProjectByYear(table.getCatalog(), table.getTableName(), table.getTableSchema(), new QueryGenerator.Where(
                projectIds, deviceIds, from, to,column, index
        ));
        return rankingProject(query, from);
    }

    @Override
    public List<DeviceRanking> rankingDeviceByDay(long[] projectIds, long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to) {
        Option<String> query = QueryGenerator.rankingDeviceByDay(table.getCatalog(), table.getTableName(), table.getTableSchema(), new QueryGenerator.Where(
                projectIds, deviceIds, from, to,column, index
        ));
        return rankingDevice(query, from);
    }

    @Override
    public List<DeviceRanking> rankingDeviceByMonth(long[] projectIds, long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to) {
        from = from.withDayOfMonth(1);
        to = to.withDayOfMonth(to.lengthOfMonth());

        Option<String> query = QueryGenerator.rankingDeviceByMonth(table.getCatalog(), table.getTableName(), table.getTableSchema(), new QueryGenerator.Where(
                projectIds, deviceIds, from, to, column, index
        ));
        return rankingDevice(query, from);
    }

    @Override
    public List<DeviceRanking> rankingDeviceByYear(long[] projectIds, long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to) {
        from = LocalDate.of(from.getYear(),1,1);
        to = LocalDate.of(to.getYear(),12,31);

        Option<String> query = QueryGenerator.rankingDeviceByYear(table.getCatalog(), table.getTableName(), table.getTableSchema(), new QueryGenerator.Where(
                projectIds, deviceIds, from, to,column, index
        ));
        return rankingDevice(query, from);
    }

    private List<ProjectRanking> rankingProject(Option<String> query, LocalDate from) {
        Problems.ensure(query.isDefined(), "列不存在！");
        return clickHouseJDBI.withHandle(handle ->
                handle.createQuery(query.get()).mapToBean(ProjectRanking.class).collect(Collectors.toList()));
    }

    private List<DeviceRanking> rankingDevice(Option<String> query, LocalDate from) {
        Problems.ensure(query.isDefined(), "列不存在！");
        return clickHouseJDBI.withHandle(handle ->
                handle.createQuery(query.get()).mapToBean(DeviceRanking.class).collect(Collectors.toList()));
    }
}
