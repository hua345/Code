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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * @author chenjianhua
 * @date 2021/8/18
 */
@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = ProducerApplication.class)
@AutoConfigureMockMvc
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testBussinessException() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        post("/testBussinessException")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        ResponseVO responseVO = JsonUtil.toBean(mvcResult.getResponse().getContentAsString(), ResponseVO.class);
        Assertions.assertNotNull(responseVO);
        Assertions.assertEquals(ResponseStatusEnum.SUCCESS.getErrorCode(), responseVO.getCode());
    }

    @Test
    void testException() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        post("/testException")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        ResponseVO responseVO = JsonUtil.toBean(mvcResult.getResponse().getContentAsString(), ResponseVO.class);
        Assertions.assertNotNull(responseVO);
        Assertions.assertEquals(ResponseStatusEnum.SERVER_ERROR.getErrorCode(), responseVO.getCode());
    }
}