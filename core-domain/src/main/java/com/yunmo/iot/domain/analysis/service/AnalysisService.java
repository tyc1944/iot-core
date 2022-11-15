package com.yunmo.iot.domain.analysis.service;

import com.yunmo.iot.domain.analysis.AnalysisTable;
import com.yunmo.iot.domain.analysis.repository.AnalysisTableRepository;
import com.yunmo.iot.domain.analysis.repository.StatisticsTableSchemaRepository;
import com.yunmo.iot.domain.analysis.value.AnalysisTableValue;
import com.yunmo.iot.domain.core.Project;
import com.yunmo.iot.schema.Schema;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalysisService {
    @Autowired
    StatisticsTableSchemaRepository schemaRepository;

    @Autowired
    AnalysisTableRepository analysisTableRepository;

    public AnalysisTable create(long productId, AnalysisTableValue value) {
        AnalysisTable analysisTable = value.create();
        analysisTable.setCatalog("iot");
        analysisTable.setTableName(String.join("_", "a", RandomStringUtils.randomAlphanumeric(8)));
        analysisTable.setProductId(productId);
        Schema characterized =  value.getTableSchema().characterized()
                .orElseThrow(()->new RuntimeException("没有任何标记特征的列"));
        analysisTable.setTableSchema(characterized);
        analysisTableRepository.save(analysisTable);
        schemaRepository.createTable(analysisTable);
        return analysisTable;
    }


    public void remove(AnalysisTable table) {
        schemaRepository.deleteTable(table.getCatalog(),table.getTableName());
        analysisTableRepository.delete(table);
    }
}
