package com.yunmo.iot.domain.analysis.repository;

import com.yunmo.iot.domain.analysis.AnalysisTable;

public interface StatisticsTableSchemaRepository {
    void createTable(AnalysisTable table);
    void createDatabase(String db);
    void deleteTable(String catalog, String table);
}
