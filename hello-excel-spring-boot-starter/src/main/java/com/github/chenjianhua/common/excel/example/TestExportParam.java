package com.github.chenjianhua.common.excel.example;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.chenjianhua.common.excel.bo.TableFieldInfoBo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author chenjianhua
 * @date 2020/12/23
 */
@Data
public class TestExportParam {
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTradeTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTradeTime;

    /**
     * 动态字段列表
     */
    private List<TableFieldInfoBo> exportFields;
}
