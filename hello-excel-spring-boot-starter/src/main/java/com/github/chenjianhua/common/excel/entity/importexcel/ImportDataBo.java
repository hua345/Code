package com.github.chenjianhua.common.excel.entity.importexcel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
@Setter
@Getter
@ToString
public class ImportDataBo {
    /**
     * 导入数据Class对象
     */
    private Class<?> modelClass;
    /**
     * 导入任务数据
     */
    private ImportTaskParam importTaskParam;

}
