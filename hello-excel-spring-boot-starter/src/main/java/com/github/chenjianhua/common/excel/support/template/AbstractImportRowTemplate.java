package com.github.chenjianhua.common.excel.support.template;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.github.chenjianhua.common.excel.bo.ipt.ImportDataBo;
import com.github.chenjianhua.common.excel.bo.ipt.ImportTaskParam;
import com.github.chenjianhua.common.excel.bo.ipt.ImportResultVo;
import com.github.chenjianhua.common.excel.support.eventlistener.AbstractModelAnalysisEventListener;
import com.github.chenjianhua.common.excel.support.ipt.ExcelImportStrategy;
import com.github.common.config.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author chenjianhua
 * @date 2021/8/25
 */
@Slf4j
public abstract class AbstractImportRowTemplate implements ExcelImportStrategy {

    /**
     * 读取的Sheet数
     */
    private String sheetName;

    /**
     * https://www.yuque.com/easyexcel/doc/read#43eb266c
     * 如果不传入class则默认为1.
     * 当然你指定了headRowNumber不管是否传入class都是以你传入的为准。
     */
    private Integer headRowNumber;

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public void setHeadRowNumber(Integer headRowNumber) {
        this.headRowNumber = headRowNumber;
    }

    public abstract AbstractModelAnalysisEventListener<Map<Integer, String>> defaultAnalysisListener(ImportDataBo importDataBo);

    /**
     * 处理全局变量时,需要重载
     */
    private void defaultImportRead(ImportDataBo importDataBo, AbstractModelAnalysisEventListener<Map<Integer, String>> excelListener) {
        // 上传的时候已经把文件流保存到temp文件
        if (null != importDataBo.getImportTaskParam().getUploadOriginTempFile()) {
            ExcelReaderBuilder excelReaderBuilder = EasyExcel.read(importDataBo.getImportTaskParam().getUploadOriginTempFile(),
                    importDataBo.getModelClass(), excelListener);
            ExcelReaderSheetBuilder excelReaderSheetBuilder;
            if (StringUtils.hasText(this.sheetName)) {
                log.info("开始读取Sheet:{}", sheetName);
                excelReaderSheetBuilder = excelReaderBuilder.sheet(this.sheetName);
            } else {
                excelReaderSheetBuilder = excelReaderBuilder.sheet();
            }
            if (null != headRowNumber) {
                excelReaderSheetBuilder.headRowNumber(headRowNumber);
            }
            excelReaderSheetBuilder.doRead();
        } else {
            throw new BusinessException("导入的文件不支持");
        }
    }

    /**
     * 实现具体的导入业务
     */
    protected final ImportResultVo excelImport(ImportDataBo importDataBo) {
        ImportResultVo importResultVo = new ImportResultVo();
        ImportTaskParam taskMeta = importDataBo.getImportTaskParam();
        importResultVo.setSyncTask(taskMeta.isSyncTask());
        importResultVo.setTaskNumber(taskMeta.getTaskNumber());
        importResultVo.setImportArg(taskMeta.getImportArg());
        // 初始化事件监听器
        AbstractModelAnalysisEventListener<Map<Integer, String>> excelListener = defaultAnalysisListener(importDataBo);
        // 进行导入处理
        defaultImportRead(importDataBo, excelListener);
        importResultVo.setSuccessRecord(excelListener.getSuccessRecord());
        importResultVo.setFailedRecord(excelListener.getFailedRecord());
        importResultVo.setTotalRecord(excelListener.getSuccessRecord() + excelListener.getFailedRecord());
        importResultVo.setResultTempFile(excelListener.getResultTempFile());
        return importResultVo;
    }

    /**
     * 执行导入
     */
    @Override
    public final ImportResultVo doImport(ImportTaskParam importTaskParam) {
        // 构建导入元数据
        ImportDataBo importDataBo = new ImportDataBo();
        importDataBo.setImportTaskParam(importTaskParam);
        // 执行导入数据
        ImportResultVo importResultVo = excelImport(importDataBo);
        return importResultVo;
    }
}
