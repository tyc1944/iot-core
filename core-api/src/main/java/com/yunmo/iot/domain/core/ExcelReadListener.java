package com.yunmo.iot.domain.core;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ExcelReadListener<T> extends AnalysisEventListener<T> {

    private Logger logger = LoggerFactory.getLogger(ExcelReadListener.class);


    List<T> list;

    public ExcelReadListener(List<T> list){
        this.list = list;
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        logger.info("解析到一条数据 -> {}",data);
        list.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        logger.info("所有数据解析完成！");
    }
}
