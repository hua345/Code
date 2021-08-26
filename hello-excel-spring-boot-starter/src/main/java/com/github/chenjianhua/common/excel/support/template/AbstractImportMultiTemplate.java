package com.github.chenjianhua.common.excel.support.template;

import com.github.chenjianhua.common.excel.bo.ipt.ImportTaskParam;
import com.github.chenjianhua.common.excel.bo.ipt.ImportResultVo;
import com.github.chenjianhua.common.excel.support.ExcelStrategySelector;
import com.github.chenjianhua.common.excel.support.ipt.ExcelImportStrategy;
import com.github.common.config.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenjianhua
 * @date 2021/8/25
 */
@Slf4j
public abstract class AbstractImportMultiTemplate implements ExcelImportStrategy {

    private List<String> importStrategyList;

    public AbstractImportMultiTemplate(List<String> importStrategyList) {
        if (CollectionUtils.isEmpty(importStrategyList)) {
            throw new BusinessException("导入策略列表不能为空");
        }
        this.importStrategyList = importStrategyList;
    }

    private void checkImportStrategyList() {
        for (String item : importStrategyList) {
            ExcelImportStrategy strategy = ExcelStrategySelector.getImportStrategy(item);
            if (null == strategy) {
                throw new BusinessException("策略" + item + "没有找到");
            }
        }
    }

    /**
     * 执行导入
     */
    @Override
    public final ImportResultVo doImport(ImportTaskParam importTaskParam) {
        checkImportStrategyList();
        ImportResultVo importResultVo = new ImportResultVo();
        importResultVo.setSyncTask(importTaskParam.isSyncTask());
        importResultVo.setTaskNumber(importTaskParam.getTaskNumber());
        importResultVo.setImportArg(importTaskParam.getImportArg());
        List<ImportResultVo> resultList = new ArrayList<>(8);
        boolean allSuccess = true;
        ImportResultVo importResult = null;
        // 执行导入数据
        for (String item : importStrategyList) {
            ExcelImportStrategy strategy = ExcelStrategySelector.getImportStrategy(item);
            ImportTaskParam itemImportTaskParam = new ImportTaskParam();
            BeanUtils.copyProperties(importTaskParam, itemImportTaskParam);
            itemImportTaskParam.setImportCode(item);
            importResult = strategy.doImport(itemImportTaskParam);
            resultList.add(importResult);
            if (importResult.getFailedRecord() >= 1) {
                allSuccess = false;
                break;
            }
        }
        // 计算各个Sheet总数
        resultList.forEach(item -> {
            importResultVo.setTotalRecord(importResultVo.getTotalRecord() + item.getTotalRecord());
            importResultVo.setFailedRecord(importResultVo.getFailedRecord() + item.getFailedRecord());
            importResultVo.setSuccessRecord(importResultVo.getSuccessRecord() + item.getSuccessRecord());
        });
        // 设置最后一个Sheet结果文件
        importResultVo.setResultTempFile(importResult.getResultTempFile());
        // 移除最后一个Sheet结果
        resultList.remove(resultList.size() - 1);
        // 删除之前Sheet结果文件
        if (!CollectionUtils.isEmpty(resultList)) {
            resultList.forEach(item -> {
                if (null != item.getResultTempFile()) {
                    item.getResultTempFile().delete();
                }
            });
        }
        return importResultVo;
    }
}
