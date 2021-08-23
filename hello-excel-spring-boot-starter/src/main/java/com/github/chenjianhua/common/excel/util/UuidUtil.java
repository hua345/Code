package com.github.chenjianhua.common.excel.util;

import java.util.UUID;

/**
 * @author chenjianhua
 * @date 2020/2/26 11:03
 */
public class UuidUtil {
    /**
     * 获取UUID
     * @return
     */
    public static String getUuid32() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }
}
