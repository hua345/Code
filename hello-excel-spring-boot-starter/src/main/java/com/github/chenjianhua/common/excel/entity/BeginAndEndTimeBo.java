package com.github.chenjianhua.common.excel.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author chenjianhua
 * @date 2020/12/22
 */
@Data
public class BeginAndEndTimeBo {

    private LocalDateTime startLocalDateTime;

    private LocalDateTime endLocalDateTime;
}
