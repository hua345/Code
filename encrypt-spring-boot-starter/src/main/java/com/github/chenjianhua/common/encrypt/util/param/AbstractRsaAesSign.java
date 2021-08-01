package com.github.chenjianhua.common.encrypt.util.param;

import lombok.Data;

/**
 * @author chenjianhua
 * @date 2021/7/2
 */
@Data
public class AbstractRsaAesSign {
    protected String encodedKey;
    protected String sign;
}
