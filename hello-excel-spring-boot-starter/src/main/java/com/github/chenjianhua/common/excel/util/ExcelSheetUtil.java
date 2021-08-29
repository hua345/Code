package com.github.chenjianhua.common.excel.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.github.chenjianhua.common.excel.entity.exportexcel.ExportDataBo;
import com.github.chenjianhua.common.excel.support.writehandler.DefaultStylesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chenjianhua
 * @date 2021/1/4
 */
@Slf4j
public class ExcelSheetUtil {
    /**
     * Integer -2147483648~2147483647
     */
    public static final Integer MAX_SHEET_NUM = 60 * 10000;

    public static final BigDecimal MAX_SHEET_NUM_BIG_DECIMAL = BigDecimal.valueOf(MAX_SHEET_NUM);

    private static final String SHEET_NAME = "sheet";

    public static WriteSheet createExcelSheet(String sheetName, Integer currentSheetNum, Class clazz) {
        if (StringUtils.isEmpty(sheetName)) {
            sheetName = SHEET_NAME + currentSheetNum;
        }
        return EasyExcel.writerSheet(currentSheetNum, sheetName).head(clazz).build();
    }

    public static WriteSheet createExcelSheet(String sheetName, Integer currentSheetNum, List<List<String>> tableHead) {
        if (StringUtils.isEmpty(sheetName)) {
            sheetName = SHEET_NAME + currentSheetNum;
        }
        return EasyExcel.writerSheet(currentSheetNum, sheetName).head(tableHead).build();
    }

    /**
     * 初始化sheet
     */
    public static WriteSheet createExportWriteSheet(Integer sheetNum, ExportDataBo exportMeta) {
        WriteSheet writeSheet;
        if (CollectionUtils.isEmpty(exportMeta.getExportFields())) {
            writeSheet = createExcelSheet(exportMeta.getSheetName() + sheetNum, sheetNum, exportMeta.getModelClass());
        } else {
            // 动态设置表头
            List<List<String>> tableHead = exportMeta.getExportFields().stream().map(item -> {
                List<String> columnHead = new ArrayList<>(16);
                columnHead.add(item.getFieldName());
                return columnHead;
            }).collect(Collectors.toList());
            writeSheet = createExcelSheet(exportMeta.getSheetName() + sheetNum, sheetNum, tableHead);
        }
        return writeSheet;
    }

    /**
     * 初始化sheet
     */
    public static WriteSheet initExportWriteSheet(ExportDataBo exportMeta) {
        return createExportWriteSheet(1, exportMeta);
    }


    public static Integer currentSheetNum(Long currentDataSize) {
        if (currentDataSize < MAX_SHEET_NUM) {
            return 1;
        } else {
            return BigDecimal.valueOf(currentDataSize).divide(MAX_SHEET_NUM_BIG_DECIMAL, RoundingMode.UP).intValue();
        }
    }

    /**
     * 检查是否切换Sheet
     */
    public static WriteSheet checkExportWriteSheet(Long currentDataSize, WriteSheet currentSheet, ExportDataBo exportMeta) {
        Integer sheetNum = ExcelSheetUtil.currentSheetNum(currentDataSize);
        if (!sheetNum.equals(currentSheet.getSheetNo())) {
            currentSheet = createExportWriteSheet(sheetNum, exportMeta);
            log.info("新增Excel Sheet页:{}", currentSheet.getSheetName());
        }
        return currentSheet;
    }

    /**
     * 创建excel写入流
     */
    public static ExcelWriter createExcelWriter(ExportDataBo exportMeta, File tempFile) throws FileNotFoundException {
        ExcelWriter excelWriter = null;
        FileOutputStream out = new FileOutputStream(tempFile);

        ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(out);

        if (CollectionUtils.isEmpty(exportMeta.getWriteHandlers())) {
            excelWriter = excelWriterBuilder.registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .registerWriteHandler(DefaultStylesUtil.defaultStyles()).build();
        } else {
            // 处理自定义样式
            exportMeta.getWriteHandlers().forEach(excelWriterBuilder::registerWriteHandler);
            excelWriter = excelWriterBuilder.build();
        }
        return excelWriter;
    }

    public static void main(String[] args) {
        log.info("currentSheetNum:{}", ExcelSheetUtil.currentSheetNum(61 * 10000L));
        log.info("currentSheetNum:{}", ExcelSheetUtil.currentSheetNum(1000L));
        int currentProgress = 10 + ((((0 + 1) * 100) / 10) * 80) / 100;
        log.info("currentProgress:{}", currentProgress);
        currentProgress = 10 + ((((9 + 1) * 100) / 10) * 80) / 100;
        ;
        log.info("currentProgress:{}", currentProgress);
    }
}
