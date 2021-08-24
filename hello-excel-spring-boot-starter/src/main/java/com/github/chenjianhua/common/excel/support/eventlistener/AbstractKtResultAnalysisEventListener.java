package com.github.chenjianhua.common.excel.support.eventlistener;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.util.StyleUtil;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.github.chenjianhua.common.excel.enums.ExcelConstants;
import com.github.chenjianhua.common.excel.util.ExcelStyleUtil;
import com.github.chenjianhua.common.excel.example.UploadDataModel;
import com.github.chenjianhua.common.excel.util.UuidUtil;
import com.github.chenjianhua.common.json.util.JsonUtil;
import com.github.common.config.exception.BusinessException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
@Slf4j
@Getter
public abstract class AbstractKtResultAnalysisEventListener<T> extends AbstractKtAnalysisEventListener<T> {
    /**
     * 表头
     */
    private Map<Integer, String> headMap;
    private Workbook workbook;
    private Sheet sheet;
    private short lastCellNum;

    private CellStyle successStyle;
    private CellStyle errorStyle;

    /**
     * 这里会一行行的返回头
     *
     * @param headMap
     * @param context
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        log.info("解析到一条头数据:{}", JsonUtil.toJsonString(headMap));
        this.headMap = headMap;
        try {
            if (null != context.readWorkbookHolder().getFile()) {
                this.workbook = WorkbookFactory.create(context.readWorkbookHolder().getFile());
            } else {
                throw new BusinessException("导入读取文件发生异常");
            }
        } catch (Exception e) {
            throw new BusinessException("文件格式错误");
        }

        this.sheet = workbook.getSheetAt(0);
        Row row = this.sheet.getRow(0);
        this.lastCellNum = row.getLastCellNum();
        // 创建头部结果Cell
        Cell headCell = row.createCell(this.lastCellNum + 1);
        WriteCellStyle headWriteCellStyle = ExcelStyleUtil.buildDefaultHeadCellStyle();
        CellStyle cellStyle = StyleUtil.buildContentCellStyle(this.workbook, headWriteCellStyle);
        headCell.setCellValue(ExcelConstants.IMPORT_RESULT_NAME);
        headCell.setCellStyle(cellStyle);

        // 创建成功和失败样式
        WriteCellStyle successWriteCellStyle = ExcelStyleUtil.buildDefaultContentCellStyle();
        // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色.头默认了 FillPatternType所以可以不指定
        successWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        successWriteCellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        this.successStyle = StyleUtil.buildContentCellStyle(workbook, successWriteCellStyle);

        WriteCellStyle errorWriteCellStyle = ExcelStyleUtil.buildDefaultContentCellStyle();
        // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色.头默认了 FillPatternType所以可以不指定
        errorWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        errorWriteCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        this.errorStyle = workbook.createCellStyle();
    }

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param rowData one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(T rowData, AnalysisContext context) {
        Integer rowNum = context.readRowHolder().getRowIndex();
        try {
            // 导入Model springboot校验注解检查
            this.checkBindingResult(rowData);
            // 检查成功，处理数据
            processData(rowData, context);
            successRecord++;
            setResult(rowNum, ExcelConstants.SUCCESS_MSG, null, this.successStyle);
        } catch (BusinessException e) {
            failedRecord++;
            log.error(String.format("excel导入第%d行%s", rowNum, e.getMessage()));
            setResult(rowNum, ExcelConstants.FAIL_MSG, e.getMessage(), this.successStyle);
        } catch (Exception e) {
            failedRecord++;
            setResult(rowNum, ExcelConstants.FAIL_MSG, null, this.successStyle);
            log.error(String.format("excel导入第%d行%s", rowNum, e.getMessage()));
            log.error(e.getMessage(), e);
        }
    }

    private void setResult(int rowNum, String result, String message, CellStyle cellStyle) {
        Row row = this.sheet.getRow(rowNum);
        Cell resultCell = row.createCell(this.lastCellNum + 1);
        resultCell.setCellValue(result);
        resultCell.setCellStyle(cellStyle);

        if (!StringUtils.isEmpty(message)) {
            Cell resultCell2 = row.createCell(this.lastCellNum + 2);
            resultCell2.setCellValue(message);
            resultCell2.setCellStyle(cellStyle);
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("所有数据解析完成！");
        if (failedRecord == 0) {
            try {
                doAfterAllProcessData(allReadRows);
            } catch (Exception e) {
                log.error("数据导入异常：", e);
                throw new BusinessException("数据插入失败");
            }
        }
        try {
            File resultFile = File.createTempFile(ExcelConstants.IMPORT_RESULT_NAME + UuidUtil.getUuid32(), ".xlsx");
            FileOutputStream fos = new FileOutputStream(resultFile);
            workbook.write(fos);
            fos.flush();
            fos.close();
            workbook.close();
            this.resultTempFile = resultFile;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException(e.getMessage());
        }
    }

    public static void main(String[] args) {
        AbstractKtAnalysisEventListener ktDefaultAnalysisEventListener = new AbstractKtResultAnalysisEventListener<UploadDataModel>() {
            @Override
            public void processData(UploadDataModel rowData, AnalysisContext context) {
                log.info(JsonUtil.toJsonString(rowData));
            }
        };
        String fileName = "/Users/chenjianhua/Desktop" + File.separator + "商品交易明细报表1275169920506234600.xlsx";

        EasyExcel.read(fileName, UploadDataModel.class, ktDefaultAnalysisEventListener).sheet().doRead();
    }
}
