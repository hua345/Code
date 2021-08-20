package com.github.chenjianhua.producer.controller;

import com.github.chenjianhua.common.json.util.JsonUtil;
import com.github.chenjianhua.producer.ProducerApplication;
import com.github.chenjianhua.producer.vo.HelloParam;
import com.github.common.resp.ResponseStatusEnum;
import com.github.common.resp.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * @author chenjianhua
 * @date 2021/8/18
 */
@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = ProducerApplication.class)
@ActiveProfiles("dev")
@AutoConfigureMockMvc
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final Charset defaultCharset = StandardCharsets.UTF_8;

    @Test
    void testPostHello() throws Exception {
        HelloParam helloParam = new HelloParam();
        helloParam.setName("fang");
        MvcResult mvcResult = mockMvc.perform(
                        post("/postHello")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JsonUtil.toJsonString(helloParam))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        ResponseVO responseVO = JsonUtil.toBean(mvcResult.getResponse().getContentAsString(defaultCharset), ResponseVO.class);
        Assertions.assertNotNull(responseVO);
        Assertions.assertEquals(ResponseStatusEnum.SUCCESS.getErrorCode(), responseVO.getCode());
        log.info("responseVO:{}", JsonUtil.toJsonString(responseVO));
    }

    @Test
    void testBusinessException() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        post("/testBusinessException")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        ResponseVO responseVO = JsonUtil.toBean(mvcResult.getResponse().getContentAsString(defaultCharset), ResponseVO.class);
        Assertions.assertNotNull(responseVO);
        Assertions.assertEquals(ResponseStatusEnum.SUCCESS.getErrorCode(), responseVO.getCode());
        log.info("responseVO:{}", JsonUtil.toJsonString(responseVO));
    }

    @Test
    void testException() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        post("/testException")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        ResponseVO responseVO = JsonUtil.toBean(mvcResult.getResponse().getContentAsString(defaultCharset), ResponseVO.class);
        Assertions.assertNotNull(responseVO);
        Assertions.assertEquals(ResponseStatusEnum.SERVER_ERROR.getErrorCode(), responseVO.getCode());
        log.info("responseVO:{}", JsonUtil.toJsonString(responseVO));
    }
}