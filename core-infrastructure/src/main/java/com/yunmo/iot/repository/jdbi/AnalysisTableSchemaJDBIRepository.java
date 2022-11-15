package com.yunmo.iot.repository.jdbi;

import com.yunmo.domain.common.Problems;
import com.yunmo.iot.domain.analysis.AnalysisTable;
import com.yunmo.iot.domain.analysis.repository.StatisticsTableSchemaRepository;
import com.yunmo.iot.pipe.clickhouse.DDLGenerator;
import com.yunmo.iot.pipe.clickhouse.Engine;
import com.yunmo.iot.pipe.clickhouse.Periods;
import com.yunmo.iot.schema.Schema;
import com.yunmo.iot.schema.SemanticCharacter;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.HandleCallback;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Slf4j
@Repository
public class AnalysisTableSchemaJDBIRepository implements StatisticsTableSchemaRepository {
    @Autowired
    private Jdbi clickHouseJDBI;

    @Override
    public void createTable(AnalysisTable table) {
        String ddl = DDLGenerator.createTable(table.getCatalog(), table.getTableName(), table.getTableSchema(), "Asia/Shanghai").get();
        Optional<Schema> schema = table.getTableSchema().characterized(null);
        try {
            clickHouseJDBI.withHandle((HandleCallback<Boolean, Exception>) handle ->{
                createDBIfNotExist(handle, table.getCatalog());
                handle.execute(ddl);
                table.getTableSchema().characterized(c->c.getGather().equals(SemanticCharacter.Gather.DISCRETE));
                schema.ifPresent(s->handle.execute(DDLGenerator.createMaterializedView(table.getCatalog(),
                        table.getTableName(),s).get()));
                schema.ifPresent(s->handle.execute(DDLGenerator.createDailyMaterializedView(table.getCatalog(),
                        table.getTableName(),s).get()));
                schema.ifPresent(s->handle.execute(DDLGenerator.createMonthlyMaterializedView(table.getCatalog(),
                        table.getTableName(),s).get()));
                return true;
            });
        } catch (Exception e) {
            log.info("创建分析表异常:{}", e);
            throw Problems.hint("创建分析表异常");
        }
    }

    public Object createDBIfNotExist(Handle handle, String db) {
        return handle.execute(String.format("create database if not exists %s on cluster 'default-cluster';", db));
    }

    @Override
    public void createDatabase(String db) {
        try {
            clickHouseJDBI.withHandle((HandleCallback<Object, Exception>) handle -> createDBIfNotExist(handle, db));
        } catch (Exception e) {
            log.info("创建分析数据库异常:{}", e);
            throw Problems.hint("创建分析数据库异常");
        }
    }

    @Override
    public void deleteTable(String catalog, String table) {
        try {
        clickHouseJDBI.useHandle(handle -> {
            handle.execute(String.format("drop table if exists %s.%s  on cluster 'default-cluster';", catalog, table));
            handle.execute(String.format("drop table if exists %s.%s_%s_mv  on cluster 'default-cluster';", catalog, table, Periods.HOURLY()));
            handle.execute(String.format("drop table if exists %s.%s_%s_mv  on cluster 'default-cluster';", catalog, table, Periods.DAILY()));
            handle.execute(String.format("drop table if exists %s.%s_%s_mv  on cluster 'default-cluster';", catalog, table, Periods.MONTHLY()));
          });
        } catch (Exception e) {
            log.info("删除数据表失败:{}", e);
            throw Problems.hint("删除数据表异常失败");
        }
    }
}
