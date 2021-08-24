package com.github.common.config;


import com.github.chenjianhua.common.json.util.JsonUtil;
import com.github.common.resp.ResponseStatusEnum;
import com.github.common.resp.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.io.File;
import java.io.OutputStream;
import java.util.Objects;

/**
 * @author chenjianhua
 * @date 2020/9/7
 */
@Slf4j
@Configuration
@ControllerAdvice
public class ResponseHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> clazz = returnType.getMethod().getReturnType();
        return clazz != ResponseVO.class
                && clazz != File.class
                && clazz != OutputStream.class
                && clazz != ResponseEntity.class
                && clazz != DeferredResult.class;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        if (response instanceof ServletServerHttpResponse && ((ServletServerHttpResponse) response).getServletResponse().getStatus() != ResponseStatusEnum.SUCCESS.getErrorCode()) {
            return body;
        }

        if (body == null) {
            if (String.class.equals(Objects.requireNonNull(returnType.getMethod()).getReturnType())) {
                return JsonUtil.toJsonString(ResponseVO.ok());
            }
            return ResponseVO.ok();
        }

        if (body instanceof OutputStream) {
            return body;
        } else if (body instanceof String) {

            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return JsonUtil.toJsonString(ResponseVO.ok(body));
        } else {
            return ResponseVO.ok(body);
        }
    }
}
