package com.github.chenjianhua.common.encrypt.util;

import com.github.chenjianhua.common.encrypt.util.param.Demo;
import com.github.chenjianhua.common.json.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author chenjianhua
 * @date 2021/7/2
 */
@Slf4j
public class HmacSha1SignTest {
    @Test
    public void hmacSha1SignTest() {
        Demo demo = new Demo();
        HmacSha1SignUtil.generateSign(demo);
        log.info("发送结果:{}", JsonUtil.toJsonString(demo));
        boolean checkResult = HmacSha1SignUtil.checkSign(demo);
        log.info("HmacSha1校验结果:{}", checkResult);
    }
}
