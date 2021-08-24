package com.github.chenjianhua.common.excel.support.ept;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.github.chenjianhua.common.excel.bo.ept.ExportDataMeta;
import com.github.chenjianhua.common.excel.bo.ept.ExportFileMeta;
import com.github.chenjianhua.common.excel.support.template.ExportTemplate;
import com.github.chenjianhua.common.excel.util.ExcelSheetUtil;
import com.github.chenjianhua.common.excel.util.ExportReflectUtil;
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
 * 简单导出抽象类
 */
@Slf4j
public abstract class AbstractExcelExport<T, P> extends ExportTemplate<T, P> {

    /**
     * 查询导出数据,参数已经解析
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
        ExportFileMeta exportFileMeta = new ExportFileMeta();
        ExcelWriter excelWriter;
        // 生成临时文件（会自动删除）
        File tempFile = null;
        try {
            // 生成临时文件（会自动删除）
            tempFile = Files.createTempFile(exportMeta.getFileName() + System.currentTimeMillis(), ExcelTypeEnum.XLSX.getValue()).toFile();
            excelWriter = ExcelSheetUtil.createExcelWriter(exportMeta, tempFile);
            // 初始化sheet
            WriteSheet writeSheet = ExcelSheetUtil.initExportWriteSheet(exportMeta);
            List<T> modelData = findExportData((P) exportMeta.getExportParam());

            if (CollectionUtils.isEmpty(modelData)) {
                modelData = Collections.emptyList();
            }
            // 写入数据
            writeSheetData(excelWriter, exportMeta, modelData, writeSheet);
            exportFileMeta.setTotalRecord((long) modelData.size());
            excelWriter.finish();
        } finally {
            exportFileMeta.setExportTimes(1);
            exportFileMeta.setExportFile(tempFile);
            exportFileMeta.setFileSize(tempFile.length());
        }

        return exportFileMeta;
    }
}
