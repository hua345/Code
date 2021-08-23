package com.github.chenjianhua.common.excel.support.writehandler;

import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.github.chenjianhua.common.excel.util.ExcelStyleUtil;
import org.apache.poi.ss.usermodel.IndexedColors;

/**
 * @author chenjianhua
 * @date 2021/3/29
 */
public class DefaultStylesUtil {
    public static HorizontalCellStyleStrategy defaultStyles() {
        //表头样式策略
        WriteCellStyle headWriteCellStyle = ExcelStyleUtil.buildDefaultHeadCellStyle();

        WriteCellStyle contentWriteCellStyle = ExcelStyleUtil.buildDefaultContentCellStyle();
        // 设置背景颜色白色
        contentWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        // 初始化表格样式
        HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        return horizontalCellStyleStrategy;
    }
}
