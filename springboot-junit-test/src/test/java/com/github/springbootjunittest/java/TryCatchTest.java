package com.github.springbootjunittest.java;

import com.github.chenjianhua.common.json.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chenjianhua
 * @date 2021/5/17
 */
@Slf4j
public class TryCatchTest {
    @Test
    public void tryCatchTest() {
        try {
            Map<String, String> map = new HashMap<>();
            map.get("aa").toString();
        } catch (NullPointerException e) {
            log.error("NullPointerException:", e);
        } catch (Exception e) {
            log.info("exception:", e);
        } finally {
            log.info("finally执行");
        }
    }
}
