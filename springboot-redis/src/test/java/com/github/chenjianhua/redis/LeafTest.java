package com.github.chenjianhua.redis;

import com.github.chenjianhua.common.id.leaf.IdLeafRedisService;
import com.github.common.util.ThreadPoolUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

/**
 * @author chenjianhua
 * @date 2020-09-07 15:41:49
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("dev")
public class LeafTest {

    private static final Logger log = LoggerFactory.getLogger(LeafTest.class);

    private final static Integer num = 100000;

    private final static Integer threadNum = 2;

    @Autowired
    private IdLeafRedisService redisLeaf;

    @Test
    public void testRedisLeaf() throws Exception {
        CountDownLatch latch = new CountDownLatch(threadNum);
        Long beginId = redisLeaf.getIdByBizTag("leaf-segment-test");
        IntStream.range(0, threadNum).forEach(i -> {
            ThreadPoolUtil.getInstance().submit(() -> {
                Long id = beginId;
                for (int j = 0; j < num; j++) {
                    Long currentId = redisLeaf.getIdByBizTag("leaf-segment-test");
                    Assertions.assertTrue(currentId > id);
                    id = currentId;
                }
                latch.countDown();
            });
        });
        latch.await();
        Long endId = redisLeaf.getIdByBizTag("leaf-segment-test");
        Long expectedId = beginId + num * threadNum + 1;
        Assertions.assertEquals(expectedId, endId);
    }
}
