package com.github.chenjianhua.common.excel.support.ipt;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.github.chenjianhua.common.excel.bo.ipt.ImportDataMeta;
import com.github.chenjianhua.common.excel.support.eventlistener.AbstractKtAnalysisEventListener;
import com.github.chenjianhua.common.excel.support.eventlistener.AbstractKtWriteFileAnalysisEventListener;
import com.github.chenjianhua.common.excel.support.template.ImportTemplate;
import com.szkunton.common.ktcommon.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
@Slf4j
public abstract class AbstractExcelImport<T> extends ImportTemplate<T> {
    /**
     * 处理单行数据，重载需要实现的
     *
     * @param rowData 解析后的单行数据
     */
    public abstract void importedRowHandle(T rowData);

    /**
     * 想要切换事件监听器的话,需要重载
     */
    @Override
    public AbstractKtAnalysisEventListener<T> defaultAnalysisListener(ImportDataMeta importDataMeta) {
        return new AbstractKtWriteFileAnalysisEventListener<T>() {
            @Override
            public void processData(T rowData, AnalysisContext context) {
                importedRowHandle(rowData);
            }
        };
    }


}
