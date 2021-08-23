package com.github.chenjianhua.common.excel.util;

import com.szkunton.common.ktjson.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author chenjianhua
 * @date 2021/3/25
 */
@Slf4j
public class ServletRespUtil {

    private static final String JSON_TYPE = "application/json;charset=utf-8";

    public static void writeJson(Object obj, HttpServletResponse response) {
        try {
            String s = JsonUtils.toJSONString(obj);
            writer(s, response);
        } catch (IOException e) {
            log.error("导入导出异常", e);
        }
    }

    public static void writer(String string, HttpServletResponse response) throws IOException {
        response.setContentType(JSON_TYPE);
        PrintWriter writer = response.getWriter();
        writer.print(string);
        writer.flush();
        writer.close();
    }
}
