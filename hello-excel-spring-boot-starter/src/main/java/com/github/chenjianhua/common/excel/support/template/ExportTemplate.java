package com.github.chenjianhua.common.excel.support.template;

import com.alibaba.excel.write.handler.WriteHandler;
import com.github.chenjianhua.common.excel.entity.exportexcel.ExportDataBo;
import com.github.chenjianhua.common.excel.entity.exportexcel.ExportFileBo;
import com.github.chenjianhua.common.excel.entity.exportexcel.ExportTaskParam;
import com.github.chenjianhua.common.excel.entity.exportexcel.ExportTaskVo;
import com.github.chenjianhua.common.excel.entity.TableFieldInfoBo;
import com.github.chenjianhua.common.excel.support.ept.ExcelExportStrategy;
import com.github.chenjianhua.common.json.util.JsonUtil;
import com.github.common.config.exception.BusinessException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ResolvableType;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
@Getter
@Slf4j
public abstract class ExportTemplate<T, P> implements ExcelExportStrategy {

    /**
     * 数据模型类
     */
    private Class<T> modelClazz;

    private Class<P> paramClazz;

    /**
     * 自定义样式处理器
     */
    private List<WriteHandler> writeHandlers;
    /**
     * 导出文件名称
     */
    private String fileName;
    /**
     * sheet名称
     */
    private String sheetName = "sheet1";

    public void setWriteHandlers(List<WriteHandler> writeHandlers) {
        this.writeHandlers = writeHandlers;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    /**
     * 初始化数据,比如异步情况下通过token获取用户信息
     */
    public void initExportData(ExportTaskParam meta) {

    }

    /**
     * 导出的具体写入excel代码
     */
    public abstract ExportFileBo excelExport(ExportDataBo exportMeta) throws IOException;

    /**
     * 构建导出元数据
     */
    private ExportDataBo buildExportData(ExportTaskParam meta, P exportParam) {
        ExportDataBo exportDataBo = new ExportDataBo();
        exportDataBo.setFileName(this.fileName);
        exportDataBo.setSheetName(this.sheetName);
        exportDataBo.setWriteHandlers(this.writeHandlers);
        exportDataBo.setExportParam(exportParam);
        exportDataBo.setTaskMeta(meta);
        exportDataBo.setModelClass(this.modelClazz);
        buildDynamicTableField(exportDataBo, exportParam);
        return exportDataBo;
    }

    /**
     * 解析范型
     */
    private void parseGenericClassType() {
        Class clz = this.getClass();
        ResolvableType resolvable = ResolvableType.forClass(clz);
        this.modelClazz = (Class<T>) resolvable.getSuperType().getGeneric(0).resolve();
        this.paramClazz = (Class<P>) resolvable.getSuperType().getGeneric(1).resolve();
    }

    /**
     * 处理查询参数
     *
     * @param jsonObj 导出任务json参数
     * @return 前端传过来的导出参数
     */
    public P buildExportParam(Object jsonObj) {
        if (null == jsonObj) {
            try {
                return this.paramClazz.newInstance();
            } catch (Exception e) {
                log.error("解析参数失败 :{}", e);
                throw new BusinessException("解析参数失败!");
            }
        }
        return JsonUtil.toBean(JsonUtil.toJsonString(jsonObj), this.paramClazz);
    }


    /**
     * 执行导出
     *
     * @param meta 导出所需的原始数据
     * @return 返回结果数据
     * @throws IOException io异常
     */
    @Override
    public final ExportTaskVo doExport(ExportTaskParam meta) throws IOException {
        // 解析范型
        parseGenericClassType();
        // 初始化数据,比如异步情况下通过token获取用户信息
        initExportData(meta);
        // 构建参数
        P exportParam = buildExportParam(meta.getExportArg());
        // 初始化一些信息
        ExportDataBo exportDataBo = buildExportData(meta, exportParam);
        // 执行导出
        ExportTaskVo exportTaskVo = doExcelExport(exportDataBo);
        // 获取导出后的信息
        completeExport(exportTaskVo);
        return exportTaskVo;
    }

    public ExportTaskVo doExcelExport(ExportDataBo exportMeta) throws IOException {
        ExportTaskVo exportTaskVo = new ExportTaskVo();
        ExportTaskParam taskMeta = exportMeta.getTaskMeta();
        exportTaskVo.setSyncTask(taskMeta.isSyncTask());
        exportTaskVo.setTaskNumber(taskMeta.getTaskNumber());
        exportTaskVo.setExportArg(taskMeta.getExportArg());
        exportTaskVo.setStartTime(LocalDateTime.now());
        // 开始导出数据
        exportTaskVo.setExportFileBo(excelExport(exportMeta));
        exportTaskVo.setFileSize(exportTaskVo.getExportFileBo().getFileSize());
        exportTaskVo.setTotalRecord(exportTaskVo.getExportFileBo().getTotalRecord());
        exportTaskVo.setEndTime(LocalDateTime.now());
        log.info("导出结果:{}", JsonUtil.toJsonString(exportTaskVo));
        return exportTaskVo;
    }

    /**
     * 自动检查动态导出字段
     */
    private void buildDynamicTableField(ExportDataBo exportDataBo, P exportParam) {
        Field dynamicTableField = ReflectionUtils.findField(this.paramClazz, "exportFields");
        if (null != dynamicTableField) {
            ResolvableType resolvableType = ResolvableType.forField(dynamicTableField);
            if (List.class.equals(resolvableType.resolve()) && TableFieldInfoBo.class.equals(resolvableType.getGeneric(0).resolve())) {
                dynamicTableField.setAccessible(true);
                try {
                    exportDataBo.setExportFields((List<TableFieldInfoBo>) dynamicTableField.get(exportParam));
                } catch (IllegalAccessException e) {
                    log.error("{}获取动态导出字段失败:{}", exportDataBo.getTaskMeta().getTaskNumber(), e);
                }
                log.info("{}动态导出字段:{}", exportDataBo.getTaskMeta().getTaskNumber(), JsonUtil.toJsonString(exportDataBo.getExportFields()));
            }

        }
    }

    /**
     * 导出完成执行,如果需要获取导出文件可以重载这个方法
     *
     * @param exportTaskVo 导出完成结果
     */
    public void completeExport(ExportTaskVo exportTaskVo) {

    }
}
