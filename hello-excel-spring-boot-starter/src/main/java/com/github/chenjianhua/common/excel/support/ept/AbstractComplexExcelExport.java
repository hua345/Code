package com.github.chenjianhua.common.excel.support.ept;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.github.chenjianhua.common.excel.bo.ept.ExportDataMeta;
import com.github.chenjianhua.common.excel.bo.ept.ExportFileMeta;
import com.github.chenjianhua.common.excel.enums.ExcelExportStatusEnum;
import com.github.chenjianhua.common.excel.service.ExcelServerRequestService;
import com.github.chenjianhua.common.excel.support.template.ExportTemplate;
import com.github.chenjianhua.common.excel.util.ApplicationContextUtil;
import com.github.chenjianhua.common.excel.util.ExcelSheetUtil;
import com.github.chenjianhua.common.excel.util.ExportReflectUtil;
import com.github.chenjianhua.common.excel.vo.UpdateExportHisResultParam;
import com.github.common.config.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chenjianhua
 * @date 2021/3/22
 * 大数据量导出
 */
@Slf4j
public abstract class AbstractComplexExcelExport<T, P> extends ExportTemplate<T, P> {

    /**
     * 处理查询参数
     * 像分页查询和时间分段，可以在这里完成
     *
     * @param param 导出任务参数
     * @return 返回分割好的查询参数，每次会调用findExportData方法
     */
    protected abstract List<P> buildComplexExportParam(P param);

    /**
     * 查询导出数据
     *
     * @param param 查询数据需要的参数
     * @return 导出Model数据列表
     */
    protected abstract List<T> findExportData(P param);

    /**
     * 写入数据
     */
    private void writeSheetData(ExcelWriter excelWriter, ExportDataMeta exportMeta, List<T> exportModelData, WriteSheet writeSheet) {
        // 写入数据
        if (CollectionUtils.isEmpty(exportMeta.getExportFields())) {
            excelWriter.write(exportModelData, writeSheet);
        } else {
            // 动态设置行数据
            List<List<Object>> rowDatas = exportModelData.stream().map(item -> {
                List<Object> valueData = new LinkedList<>();
                exportMeta.getExportFields().forEach(fieldItem -> valueData.add(ExportReflectUtil.getObjectValue(item, fieldItem)));
                return valueData;
            }).collect(Collectors.toList());
            excelWriter.write(rowDatas, writeSheet);
        }
    }

    @Override
    public ExportFileMeta excelExport(ExportDataMeta exportMeta) throws IOException {
        List<P> params = buildComplexExportParam((P) exportMeta.getExportParam());
        if (CollectionUtils.isEmpty(params)) {
            throw new BusinessException("大数据量导出参数为空");
        }
        ExportFileMeta exportFileMeta = new ExportFileMeta();
        ExcelWriter excelWriter;
        // 生成临时文件（会自动删除）
        File tempFile = null;
        long currentDataSize = 0L;
        try {
            // 生成临时文件（会自动删除）
            tempFile = Files.createTempFile(exportMeta.getFileName() + System.currentTimeMillis(), ExcelTypeEnum.XLSX.getValue()).toFile();
            excelWriter = ExcelSheetUtil.createExcelWriter(exportMeta, tempFile);
            // 初始化sheet
            WriteSheet currentSheet = ExcelSheetUtil.initExportWriteSheet(exportMeta);
            for (int index = 0; index < params.size(); index++) {
                P param = params.get(index);
                List<T> rows = findExportData(param);
                if (CollectionUtils.isEmpty(rows)) {
                    rows = Collections.emptyList();
                }
                // 检查是否切换Sheet
                currentDataSize = currentDataSize + rows.size();
                currentSheet = ExcelSheetUtil.checkExportWriteSheet(currentDataSize, currentSheet, exportMeta);
                // 处理导出进度
                processHandle(params, index, exportMeta);
                // 写入数据
                writeSheetData(excelWriter, exportMeta, rows, currentSheet);
            }
            exportFileMeta.setExportTimes(params.size());
            excelWriter.finish();
        } finally {
            exportFileMeta.setExportFile(tempFile);
            exportFileMeta.setFileSize(tempFile.length());
            exportFileMeta.setTotalRecord(currentDataSize);
        }
        return exportFileMeta;
    }

    /**
     * 处理导出进度
     */
    private void processHandle(List<P> params, Integer index, ExportDataMeta exportMeta) {
        ExcelServerRequestService excelServerRequestService = ApplicationContextUtil.getBean(ExcelServerRequestService.class);
        UpdateExportHisResultParam updateExportHisResultParam = new UpdateExportHisResultParam();
        updateExportHisResultParam.setTaskNumber(exportMeta.getTaskMeta().getTaskNumber());
        updateExportHisResultParam.setExportStatus(ExcelExportStatusEnum.DOING);
        // 从10%到90%,设置导出进度
        int currentProgress = 10 + ((((index + 1) * 100) / params.size()) * 80) / 100;
        updateExportHisResultParam.setExportProgress(currentProgress);
        updateExportHisResultParam.setResultMsg("正在写入excel文件");
        excelServerRequestService.updateExportHisResult(updateExportHisResultParam);
    }
}
