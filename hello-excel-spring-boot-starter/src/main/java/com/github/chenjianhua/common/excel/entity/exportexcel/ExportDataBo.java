package com.github.chenjianhua.common.excel.entity.exportexcel;

import com.alibaba.excel.write.handler.WriteHandler;
import com.github.chenjianhua.common.excel.entity.TableFieldInfoBo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
@Setter
@Getter
@ToString
public class ExportDataBo {
    /**
     * 导出数据Class对象
     */
    private Class<?> modelClass;

    /**
     * 导出参数
     */
    private Object exportParam;
    /**
     * 动态字段列表，仅用于动态导出
     */
    private List<TableFieldInfoBo> exportFields;
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
    /**
     * 导出任务数据
     */
    private ExportTaskParam taskMeta;

}
