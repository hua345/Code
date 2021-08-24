package com.github.chenjianhua.common.excel.support.ipt;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.github.chenjianhua.common.excel.bo.ipt.ImportDataMeta;
import com.github.chenjianhua.common.excel.support.eventlistener.AbstractKtAnalysisEventListener;
import com.github.chenjianhua.common.excel.support.eventlistener.AbstractKtWriteFileAnalysisEventListener;
import com.github.chenjianhua.common.excel.support.template.ImportTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
@Slf4j
public abstract class AbstractExcelImportAll<T> extends ImportTemplate<T> {
    /**
     * 处理单行数据检查，重载需要实现的
     *
     * @param rowData 解析后的单行数据
     */
    public abstract void rowDataCheck(T rowData);

    /**
     * 如果需要检查完所有数据后，返回所有的数据,需要重载
     *
     * @param rowData 检查成功后返回的所有数据
     */
    public abstract void importedAllRowHandle(List<T> rowData);

    /**
     * 想要切换事件监听器的话,需要重载
     */
    @Override
    public AbstractKtAnalysisEventListener<T> defaultAnalysisListener(ImportDataMeta importDataMeta) {
        return new AbstractKtWriteFileAnalysisEventListener<T>(true) {
            @Override
            public void processData(T rowData, AnalysisContext context) {
                rowDataCheck(rowData);
            }

            @Override
            public void doAfterAllProcessData(List<T> rowDatas) {
                importedAllRowHandle(rowDatas);
            }
        };
    }
}
