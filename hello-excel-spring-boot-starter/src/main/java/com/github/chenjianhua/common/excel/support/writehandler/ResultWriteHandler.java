package com.github.chenjianhua.common.excel.support.writehandler;

import com.alibaba.excel.util.StyleUtil;
import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.github.chenjianhua.common.excel.enums.ExcelConstants;
import com.github.chenjianhua.common.excel.util.ExcelStyleUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.util.StringUtils;


/**
 * @author chenjianhua
 * @date 2021/3/28
 */
@Slf4j
public class ResultWriteHandler implements RowWriteHandler {

    @Override
    public void beforeRowCreate(WriteSheetHolder var1, WriteTableHolder var2, Integer var3, Integer var4, Boolean var5) {

    }

    @Override
    public void afterRowCreate(WriteSheetHolder var1, WriteTableHolder var2, Row var3, Integer var4, Boolean var5) {

    }

    @Override
    public void afterRowDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer relativeRowIndex, Boolean isHead) {
        if (isHead) {
            return;
        }
        Cell cell = row.getCell(row.getLastCellNum() - 1);
        if (null == cell) {
            return;
        }
        String cellValue = cell.getStringCellValue();
        if (!StringUtils.hasText(cellValue)) {
            return;
        }
        Workbook workbook = writeSheetHolder.getSheet().getWorkbook();
        WriteCellStyle contentWriteCellStyle = ExcelStyleUtil.buildDefaultContentCellStyle();
        // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色.头默认了 FillPatternType所以可以不指定
        contentWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        if (ExcelConstants.SUCCESS_MSG.equals(cellValue)) {
            contentWriteCellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            CellStyle cellStyle = StyleUtil.buildContentCellStyle(workbook, contentWriteCellStyle);
            cell.setCellStyle(cellStyle);
        } else {
            contentWriteCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
            CellStyle cellStyle = StyleUtil.buildContentCellStyle(workbook, contentWriteCellStyle);
            cell.setCellStyle(cellStyle);
        }
    }
}