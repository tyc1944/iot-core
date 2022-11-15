package com.yunmo.iot.domain.analysis.repository;

import com.yunmo.domain.common.EntityRepository;
import com.yunmo.iot.domain.analysis.AnalysisTable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AnalysisTableRepository extends EntityRepository<AnalysisTable, Long> {
    Page<AnalysisTable> findAllByProductId(Long id, Pageable paging);

    List<AnalysisTable> findAllByProductId(Long id);
}
