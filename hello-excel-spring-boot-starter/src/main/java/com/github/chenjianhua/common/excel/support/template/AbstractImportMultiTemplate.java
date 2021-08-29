package com.github.chenjianhua.common.excel.support.template;

import com.github.chenjianhua.common.excel.entity.importexcel.ImportTaskParam;
import com.github.chenjianhua.common.excel.entity.importexcel.ImportTaskVo;
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
    public final ImportTaskVo doImport(ImportTaskParam importTaskParam) {
        checkImportStrategyList();
        ImportTaskVo importTaskVo = new ImportTaskVo();
        importTaskVo.setSyncTask(importTaskParam.isSyncTask());
        importTaskVo.setTaskNumber(importTaskParam.getTaskNumber());
        importTaskVo.setImportArg(importTaskParam.getImportArg());
        List<ImportTaskVo> resultList = new ArrayList<>(8);
        boolean allSuccess = true;
        ImportTaskVo importResult = null;
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
            importTaskVo.setTotalRecord(importTaskVo.getTotalRecord() + item.getTotalRecord());
            importTaskVo.setFailedRecord(importTaskVo.getFailedRecord() + item.getFailedRecord());
            importTaskVo.setSuccessRecord(importTaskVo.getSuccessRecord() + item.getSuccessRecord());
        });
        // 设置最后一个Sheet结果文件
        importTaskVo.setResultTempFile(importResult.getResultTempFile());
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
        return importTaskVo;
    }
}
