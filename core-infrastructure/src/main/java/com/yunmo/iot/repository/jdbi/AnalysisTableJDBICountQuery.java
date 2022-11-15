package com.yunmo.iot.repository.jdbi;

import com.yunmo.domain.common.Problems;
import com.yunmo.iot.domain.analysis.AnalysisTable;
import com.yunmo.iot.domain.analysis.repository.AnalysisTableCountQuery;
import com.yunmo.iot.domain.analysis.value.AggregateCount;
import com.yunmo.iot.domain.analysis.value.HourlyCount;
import com.yunmo.iot.pipe.clickhouse.QueryGenerator;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import scala.Option;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class AnalysisTableJDBICountQuery implements AnalysisTableCountQuery {
    @Autowired
    Jdbi clickHouseJDBI;

    public static final class AggregateCountMapper implements RowMapper<AggregateCount> {

        @Override
        public AggregateCount map(ResultSet rs, StatementContext ctx) throws SQLException {

            return new AggregateCount(rs.getDate("__date"),
                    rs.getArray("key").getArray(),
                        (BigInteger[])rs.getArray("count").getArray());
        }
    }

    public static final class HourlyCountMapper implements RowMapper<HourlyCount> {

        @Override
        public HourlyCount map(ResultSet rs, StatementContext ctx) throws SQLException {
            return HourlyCount.builder()
                    .__hour(rs.getInt("__hour"))
                    .__date(rs.getDate("__date"))
                    .key(rs.getArray("key").getArray())
                    .count((BigInteger[])rs.getArray("count").getArray())
                    .build();
        }
    }

    @Override
    public List<HourlyCount> selectHourly(long[] projectIds, long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to) {
        Option<String> query = QueryGenerator.selectHourly(table.getCatalog(), table.getTableName(), table.getTableSchema(), new QueryGenerator.Where(
                projectIds, deviceIds, from, to, column, index
        ));
        Problems.ensure(query.isDefined(), "列不存在！");
        return clickHouseJDBI.withHandle(handle ->
                handle.createQuery(query.get()).map(new HourlyCountMapper()).collect(Collectors.toList()));
    }

    @Override
    public List<AggregateCount> selectDaily(long[] projectIds,long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to) {
        Option<String> query = QueryGenerator.selectDaily(table.getCatalog(), table.getTableName(), table.getTableSchema(), new QueryGenerator.Where(
                projectIds, deviceIds, from, to,column, index
        ));
        return query(query, from);
    }

    @Override
    public List<AggregateCount> selectWeekly(long[] projectIds,long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to) {
        from = from.with(DayOfWeek.MONDAY);
        to = to.with(DayOfWeek.SUNDAY);

        Option<String> query = QueryGenerator.selectWeekly(table.getCatalog(), table.getTableName(), table.getTableSchema(), new QueryGenerator.Where(
                projectIds, deviceIds, from, to, column, index
        ));

        return query(query, from);
    }

    private List<AggregateCount> query(Option<String> query, LocalDate from) {
        Problems.ensure(query.isDefined(), "列不存在！");
        return clickHouseJDBI.withHandle(handle ->
                handle.createQuery(query.get())
                        .map(new AggregateCountMapper()).collect(Collectors.toList()));
    }

    @Override
    public List<AggregateCount> selectMonthly(long[] projectIds,long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to) {
        from = from.withDayOfMonth(1);
        to = to.withDayOfMonth(to.lengthOfMonth());

        Option<String> query = QueryGenerator.selectMonthly(table.getCatalog(), table.getTableName(), table.getTableSchema(), new QueryGenerator.Where(
                projectIds, deviceIds, from, to, column, index
        ));
        return query(query, from);
    }

    @Override
    public List<AggregateCount> selectQuarterly(long[] projectIds,long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to) {
        from = LocalDate.of(from.getYear(), from.getMonth().firstMonthOfQuarter(), 1);
        Month toMonth = to.getMonth().firstMonthOfQuarter().plus(2);
        to = LocalDate.of(to.getYear(), toMonth, toMonth.length(to.isLeapYear()));

        Option<String> query = QueryGenerator.selectQuarterly(table.getCatalog(), table.getTableName(), table.getTableSchema(), new QueryGenerator.Where(
                projectIds, deviceIds, from, to,column, index
        ));
        return query(query, from);
    }

    @Override
    public List<AggregateCount> selectYearly(long[] projectIds,long[] deviceIds, AnalysisTable table, String column, int[] index, LocalDate from, LocalDate to) {
        from = LocalDate.of(from.getYear(),1,1);
        to = LocalDate.of(to.getYear(),12,31);

        Option<String> query = QueryGenerator.selectYearly(table.getCatalog(), table.getTableName(), table.getTableSchema(), new QueryGenerator.Where(
                projectIds, deviceIds, from, to,column, index
        ));
        return query(query, from);
    }
}
