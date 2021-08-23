package com.github.chenjianhua.common.excel.bo.ipt;

import com.alibaba.excel.event.AnalysisEventListener;
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
public class ImportDataMeta {
    /**
     * 导入数据Class对象
     */
    private Class<?> modelClass;
    /**
     * 导入任务数据
     */
    private ImportTaskMeta taskMeta;

}
