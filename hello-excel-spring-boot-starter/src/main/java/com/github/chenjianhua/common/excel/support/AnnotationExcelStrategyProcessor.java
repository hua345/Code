package com.github.chenjianhua.common.excel.support;

import com.github.chenjianhua.common.excel.annotation.ExportStrategy;
import com.github.chenjianhua.common.excel.support.ipt.ExcelImportStrategy;
import com.github.chenjianhua.common.excel.annotation.ImportStrategy;
import com.github.chenjianhua.common.excel.support.ept.ExcelExportStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
@Slf4j
@Configuration
public class AnnotationExcelStrategyProcessor implements ApplicationRunner, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("start scan annotation @ExcelExportStrategy class.");
        exportStrategyScan();
        importStrategyScan();
    }

    /**
     * 导出策略扫描
     */
    private void exportStrategyScan() {
        Class<? extends Annotation> annotationClass = ExportStrategy.class;
        Map<String,Object> beanWhithAnnotation = applicationContext.getBeansWithAnnotation(annotationClass);
        Set<Map.Entry<String, Object>> entitySet = beanWhithAnnotation.entrySet();
        for (Map.Entry<String, Object> entry :entitySet){

            Class<? extends ExcelExportStrategy> clazz = (Class<? extends ExcelExportStrategy>)entry.getValue().getClass();
            ExportStrategy exportStrategyAnnotation = AnnotationUtils.findAnnotation(clazz, ExportStrategy.class);
            if(StringUtils.isEmpty(exportStrategyAnnotation.strategyCode())) {
                continue;
            }

            ExcelExportStrategy excelExportStrategy = applicationContext.getBean(clazz);
            ExcelStrategySelector.putExportStrategy(exportStrategyAnnotation.strategyCode(), excelExportStrategy);
        }
    }

    /**
     * 导入策略扫描
     */
    private void importStrategyScan() {
        Class<? extends Annotation> annotationClass = ImportStrategy.class;
        Map<String,Object> beanWhithAnnotation = applicationContext.getBeansWithAnnotation(annotationClass);
        Set<Map.Entry<String, Object>> entitySet = beanWhithAnnotation.entrySet();
        for (Map.Entry<String, Object> entry :entitySet){

            Class<? extends ExcelImportStrategy> clazz = (Class<? extends ExcelImportStrategy>)entry.getValue().getClass();
            ImportStrategy exportStrategyAnnotation = AnnotationUtils.findAnnotation(clazz, ImportStrategy.class);
            if(StringUtils.isEmpty(exportStrategyAnnotation.strategyCode())) {
                continue;
            }

            ExcelImportStrategy excelImportStrategy = applicationContext.getBean(clazz);
            ExcelStrategySelector.putImportStrategy(exportStrategyAnnotation.strategyCode(), excelImportStrategy);
        }
    }
}