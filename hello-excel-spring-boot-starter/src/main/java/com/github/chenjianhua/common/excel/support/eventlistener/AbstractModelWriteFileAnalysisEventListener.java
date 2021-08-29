package com.github.chenjianhua.common.excel.support.eventlistener;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.github.chenjianhua.common.excel.enums.ExcelConstants;
import com.github.chenjianhua.common.excel.support.writehandler.DefaultStylesUtil;
import com.github.chenjianhua.common.excel.support.writehandler.ResultWriteHandler;
import com.github.chenjianhua.common.excel.util.ExcelSheetUtil;
import com.github.chenjianhua.common.excel.util.ExportReflectUtil;
import com.github.chenjianhua.common.excel.util.UuidUtil;
import com.github.chenjianhua.common.json.util.JsonUtil;
import com.github.common.config.exception.BusinessException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
@Slf4j
@Getter
public abstract class AbstractModelWriteFileAnalysisEventListener<T> extends AbstractModelAnalysisEventListener<T> {
    /**
     * 表头
     */
    private Map<Integer, String> headMap;
    /**
     * excel写入流
     */
    private ExcelWriter excelWriter;
    private WriteSheet writeSheet;

    private AbstractModelWriteFileAnalysisEventListener() {
    }

    public AbstractModelWriteFileAnalysisEventListener(Boolean readAllRows) {
        this.readAllRows = readAllRows;
    }

    public AbstractModelWriteFileAnalysisEventListener(Boolean readAllRows, Integer beginReadRow) {
        this.readAllRows = readAllRows;
        this.beginReadRow = beginReadRow;
    }

    /**
     * 这里会一行行的返回头
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        log.info("解析到一条头数据:{}", JsonUtil.toJsonString(headMap));
        this.headMap = headMap;
        // 构建Excel结果头
        List<List<String>> tableHead = buildExcelHeadList(headMap, context);
        tableHead.add(Collections.singletonList(ExcelConstants.IMPORT_RESULT_NAME));
        // 构建excel结果文件
        buildExcelResultFile(tableHead, context);
    }

    /**
     * 构建excel结果文件
     */
    private void buildExcelResultFile(List<List<String>> tableHead, AnalysisContext context) {
        if (null != this.resultTempFile) {
            return;
        }
        try {
            this.resultTempFile = Files.createTempFile(ExcelConstants.IMPORT_RESULT_NAME + UuidUtil.getUuid32(), ExcelTypeEnum.XLSX.getValue()).toFile();
        } catch (IOException e) {
            log.error("创建temp文件失败:{}", e);
        }
        this.writeSheet = ExcelSheetUtil.createExcelSheet(context.readSheetHolder().getSheetName(), context.readSheetHolder().getSheetNo(), tableHead);

        this.excelWriter = EasyExcel.write(this.resultTempFile)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .registerWriteHandler(DefaultStylesUtil.defaultStyles())
                .registerWriteHandler(new ResultWriteHandler())
                .build();
    }

    /**
     * 构建Excel结果头
     */
    private List<List<String>> buildExcelHeadList(Map<Integer, String> headMap, AnalysisContext context) {
        List<List<String>> tableHead = new LinkedList<>();
        if (null != context.readWorkbookHolder().getClazz()) {
            // 动态设置表头
            Field[] fields = context.readWorkbookHolder().getClazz().getDeclaredFields();
            for (Field field : fields) {
                ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
                tableHead.add(Arrays.asList(excelProperty.value()));
            }
        } else {
            headMap.values().forEach(item -> {
                tableHead.add(Arrays.asList(item));
            });
        }
        return tableHead;
    }

    private boolean checkBeginData(T rowData, AnalysisContext context) {
        Integer rowNum = context.readRowHolder().getRowIndex() + 1;
        if (null != beginReadRow && rowNum < beginReadRow) {
            log.info("忽略当前行:{} rowData:{}", rowNum, JsonUtil.toJsonString(rowData));
            // 动态写入结果文件
            List<Object> rowCellList = buildRowList(rowData, context);
            // 写入行数据结果
            writeRowResult(rowCellList, context);
            return true;
        }
        return false;
    }

    /**
     * 这个每一条数据解析都会来调用
     */
    @Override
    public void invoke(T rowData, AnalysisContext context) {
        if (checkBeginData(rowData, context)) {
            return;
        }
        String resultMsg = "";
        try {
            // 导入Model springboot校验注解检查
            this.checkBindingResult(rowData);
            // 检查成功，处理数据
            processData(rowData, context);
            successRecord++;
            if (this.readAllRows) {
                this.allReadRows.add(rowData);
            }
            resultMsg = ExcelConstants.SUCCESS_MSG;
        } catch (BusinessException e) {
            failedRecord++;
            resultMsg = e.getMessage();
            log.error(String.format("excel导入第%d行%s", context.readRowHolder().getRowIndex(), e.getMessage()));
        } catch (Exception e) {
            failedRecord++;
            resultMsg = ExcelConstants.FAIL_MSG;
            log.error(String.format("excel导入第%d行%s", context.readRowHolder().getRowIndex(), e.getMessage()));
            log.error(e.getMessage(), e);
        }
        // 动态写入结果文件
        List<Object> rowCellList = buildRowList(rowData, context);
        rowCellList.add(resultMsg);
        // 写入行数据结果
        writeRowResult(rowCellList, context);
    }

    /**
     * 写入行数据结果
     */
    private void writeRowResult(List<Object> rowCellList, AnalysisContext context) {
        if (null != excelWriter && null != writeSheet) {
            excelWriter.write(Collections.singletonList(rowCellList), writeSheet);
        } else {
            List<List<String>> headList = new ArrayList<>();
            buildExcelResultFile(headList, context);
            excelWriter.write(Collections.singletonList(rowCellList), writeSheet);
        }
    }

    /**
     * 构建行数据
     */
    private List<Object> buildRowList(T rowData, AnalysisContext context) {
        List<Object> rowCellList;
        if (null != context.readWorkbookHolder().getClazz()) {
            rowCellList = ExportReflectUtil.getClassFieldValue(rowData);
        } else if (rowData instanceof Map) {
            Map<String, Object> map = (Map) rowData;
            rowCellList = map.values().stream().collect(Collectors.toList());
        } else {
            log.info("unknown data type");
            rowCellList = new ArrayList<>(4);
        }
        return rowCellList;
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
        excelWriter.finish();
        log.info(this.resultTempFile.getAbsolutePath());
    }
}
