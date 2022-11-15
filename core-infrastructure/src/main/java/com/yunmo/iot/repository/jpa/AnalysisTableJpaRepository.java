package com.yunmo.iot.repository.jpa;

import com.yunmo.iot.domain.analysis.AnalysisTable;
import com.yunmo.iot.domain.analysis.repository.AnalysisTableRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisTableJpaRepository extends JpaRepository<AnalysisTable, Long>, AnalysisTableRepository {
}
