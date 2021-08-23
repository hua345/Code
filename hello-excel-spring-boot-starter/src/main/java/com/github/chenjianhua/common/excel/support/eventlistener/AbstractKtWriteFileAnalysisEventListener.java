package com.github.chenjianhua.common.excel.support.eventlistener;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.github.chenjianhua.common.excel.enums.ExcelConstants;
import com.szkunton.common.ktcommon.exception.BusinessException;
import com.github.chenjianhua.common.excel.example.UploadDataModel;
import com.github.chenjianhua.common.excel.support.writehandler.DefaultStylesUtil;
import com.github.chenjianhua.common.excel.support.writehandler.ResultWriteHandler;
import com.github.chenjianhua.common.excel.util.ExcelSheetUtil;
import com.github.chenjianhua.common.excel.util.ExportReflectUtil;
import com.github.chenjianhua.common.excel.util.UuidUtil;
import com.szkunton.common.ktjson.util.JsonUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.*;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
@Slf4j
@Getter
public abstract class AbstractKtWriteFileAnalysisEventListener<T> extends AbstractKtAnalysisEventListener<T> {
    /**
     * 表头
     */
    private Map<Integer, String> headMap;
    /**
     * excel写入流
     */
    private ExcelWriter excelWriter;
    private WriteSheet writeSheet;

    /**
     * 检查所有数据后是否返回所有数据
     */
    private Boolean readAllRows = false;

    public AbstractKtWriteFileAnalysisEventListener() {

    }

    public AbstractKtWriteFileAnalysisEventListener(Boolean readAllRows) {
        this.readAllRows = readAllRows;
    }

    /**
     * 这里会一行行的返回头
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        log.info("解析到一条头数据:{}", JsonUtils.toJSONString(headMap));
        this.headMap = headMap;
        try {
            this.resultTempFile = Files.createTempFile(ExcelConstants.IMPORT_RESULT_NAME + UuidUtil.getUuid32(), ExcelTypeEnum.XLSX.getValue()).toFile();
        } catch (IOException e) {
            log.error("创建temp文件失败:{}", e);
        }

        // 动态设置表头
        Field[] fields = context.readWorkbookHolder().getClazz().getDeclaredFields();
        List<List<String>> tableHead = new LinkedList<>();
        for (Field field : fields) {
            ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
            tableHead.add(Arrays.asList(excelProperty.value()));
        }
        tableHead.add(Collections.singletonList(ExcelConstants.IMPORT_RESULT_NAME));

        this.writeSheet = ExcelSheetUtil.createExcelSheet(context.readSheetHolder().getSheetName(), 1, tableHead);
        this.excelWriter = EasyExcel.write(this.resultTempFile)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .registerWriteHandler(DefaultStylesUtil.defaultStyles())
                .registerWriteHandler(new ResultWriteHandler())
                .build();
    }

    /**
     * 这个每一条数据解析都会来调用
     */
    @Override
    public void invoke(T rowData, AnalysisContext context) {
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
        List<Object> rowCellList = ExportReflectUtil.getClassFieldValue(rowData);
        rowCellList.add(resultMsg);
        excelWriter.write(Collections.singletonList(rowCellList), writeSheet);
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
                throw new BusinessException("数据插入失败", e);
            }
        }
        excelWriter.finish();
        log.info(this.resultTempFile.getAbsolutePath());
    }

    public static void main(String[] args) {
        String fileName = "/Users/chenjianhua/Desktop" + File.separator + "商品交易明细报表1275169920506234600.xlsx";
        AbstractKtAnalysisEventListener ktDefaultAnalysisEventListener = new AbstractKtWriteFileAnalysisEventListener<UploadDataModel>(true) {
            @Override
            public void processData(UploadDataModel rowData, AnalysisContext context) {
                log.info(JsonUtils.toJSONString(rowData));
            }

            @Override
            public void doAfterAllProcessData(List<UploadDataModel> rowDatas) {
                log.info("读取的数据行数:{}", rowDatas.size());
            }
        };
        EasyExcel.read(fileName, UploadDataModel.class, ktDefaultAnalysisEventListener).sheet().doRead();
    }
}
