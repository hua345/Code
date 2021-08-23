package com.github.chenjianhua.common.mybatisplus;

import com.github.chenjianhua.common.json.util.JsonUtil;
import com.github.chenjianhua.common.mybatisplus.vo.PageVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenjianhua
 * @date 2021/8/20
 */
@Slf4j
public class PageVoTest {

    @Test
    public void testPageVo() {
        PageVo<Map> pageVo = new PageVo();
        List<Map> maps = new ArrayList<>();
        Map<String, String> testMap = new HashMap<>();
        testMap.put("test", "test");
        maps.add(testMap);
        pageVo.setCurrentPage(1);
        pageVo.setTotal(20);
        pageVo.setPageSize(10);
        pageVo.setRows(maps);
        PageVo pageVoNew = JsonUtil.toBean(JsonUtil.toJsonString(pageVo), PageVo.class);
        Assertions.assertTrue(JsonUtil.toJsonString(pageVo).equals(JsonUtil.toJsonString(pageVoNew)));
        Assertions.assertEquals(1, pageVoNew.getCurrentPage());
        Assertions.assertEquals(20, pageVoNew.getTotal());
        Assertions.assertEquals(10, pageVoNew.getPageSize());
        Assertions.assertEquals(2, pageVoNew.getTotalPage());
    }
}
