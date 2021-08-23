package com.github.chenjianhua.common.excel.support;

import com.github.chenjianhua.common.excel.support.ipt.ExcelImportStrategy;
import com.github.chenjianhua.common.excel.support.ept.ExcelExportStrategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
public class ExcelStrategySelector {

    private static final Map<String, ExcelExportStrategy> EXPORT_STRATEGY_MAP = new ConcurrentHashMap<>();

    private static final Map<String, ExcelImportStrategy> IMPORT_STRATEGY_MAP = new ConcurrentHashMap<>();

    public static ExcelExportStrategy getExportStrategy(String strategyCode) {
        return EXPORT_STRATEGY_MAP.get(strategyCode);
    }

    public static void putExportStrategy(String strategyCode, ExcelExportStrategy strategy) {
        EXPORT_STRATEGY_MAP.put(strategyCode, strategy);
    }

    public static ExcelImportStrategy getImportStrategy(String strategyCode) {
        return IMPORT_STRATEGY_MAP.get(strategyCode);
    }

    public static void putImportStrategy(String strategyCode, ExcelImportStrategy strategy) {
        IMPORT_STRATEGY_MAP.put(strategyCode, strategy);
    }
}
