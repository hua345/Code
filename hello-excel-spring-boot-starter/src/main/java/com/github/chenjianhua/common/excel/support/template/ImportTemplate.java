package com.github.chenjianhua.common.excel.support.template;

import com.alibaba.excel.EasyExcel;
import com.github.chenjianhua.common.excel.bo.ipt.ImportDataMeta;
import com.github.chenjianhua.common.excel.bo.ipt.ImportTaskMeta;
import com.github.chenjianhua.common.excel.bo.ipt.ImportedMeta;
import com.github.chenjianhua.common.excel.support.eventlistener.AbstractKtAnalysisEventListener;
import com.github.chenjianhua.common.excel.support.ipt.ExcelImportStrategy;
import com.github.common.config.exception.BusinessException;
import org.springframework.core.ResolvableType;

import java.io.IOException;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
public abstract class ImportTemplate<T> implements ExcelImportStrategy {

    /**
     * 数据模型类
     */
    private Class<T> modelClazz;

    /**
     * 解析范型
     */
    private void parseGenericClassType() {
        Class clz = this.getClass();
        ResolvableType resolvable = ResolvableType.forClass(clz);
        this.modelClazz = (Class<T>) resolvable.getSuperType().getGeneric(0).resolve();
    }

    /**
     * 初始化导入任务
     */
    public void initImportData(ImportTaskMeta meta) {

    }

    public abstract AbstractKtAnalysisEventListener<T> defaultAnalysisListener(ImportDataMeta importDataMeta);

    /**
     * 处理全局变量时,需要重载
     */
    public void defaultImportRead(ImportDataMeta importDataMeta, AbstractKtAnalysisEventListener<T> excelListener) {
        //
        // 上传的时候已经把文件流保存到temp文件
        if (null != importDataMeta.getTaskMeta().getUploadOriginTempFile()) {
            EasyExcel.read(importDataMeta.getTaskMeta().getUploadOriginTempFile(), importDataMeta.getModelClass(), excelListener).sheet().doRead();
        } else {
            throw new BusinessException("导入的文件不支持");
        }
    }

    /**
     * 实现具体的导入业务
     */
    public final ImportedMeta excelImport(ImportDataMeta importDataMeta) {
        ImportedMeta importedMeta = new ImportedMeta();
        ImportTaskMeta taskMeta = importDataMeta.getTaskMeta();
        importedMeta.setSyncTask(taskMeta.isSyncTask());
        importedMeta.setTaskNumber(taskMeta.getTaskNumber());
        importedMeta.setImportArg(taskMeta.getImportArg());
        // 初始化事件监听器
        AbstractKtAnalysisEventListener<T> excelListener = defaultAnalysisListener(importDataMeta);
        // 进行导入处理
        defaultImportRead(importDataMeta, excelListener);
        importedMeta.setSuccessRecord(excelListener.getSuccessRecord());
        importedMeta.setFailedRecord(excelListener.getFailedRecord());
        importedMeta.setTotalRecord(excelListener.getSuccessRecord() + excelListener.getFailedRecord());
        importedMeta.setResultTempFile(excelListener.getResultTempFile());
        return importedMeta;
    }

    /**
     * 执行导入
     */
    @Override
    public final ImportedMeta doImport(ImportTaskMeta meta) throws IOException {
        // 解析范型
        parseGenericClassType();
        // 构建导入元数据
        ImportDataMeta importDataMeta = new ImportDataMeta();
        importDataMeta.setTaskMeta(meta);
        importDataMeta.setModelClass(this.modelClazz);
        // 执行可以重载的方法,初始化一些数据
        initImportData(meta);
        // 执行导入数据
        ImportedMeta importedMeta = excelImport(importDataMeta);
        // 执行可以重载的方法,导入完可以进行的方法
        completeImport(importedMeta);

        return importedMeta;
    }

    /**
     * 导入完成执行
     *
     * @param importedMeta 导入结果信息
     */
    public void completeImport(ImportedMeta importedMeta) {

    }
}
