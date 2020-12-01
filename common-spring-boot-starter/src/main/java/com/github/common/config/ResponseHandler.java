package com.github.common.config;


import com.github.common.resp.ResponseVO;
import com.github.common.util.JsonUtil;
import com.github.common.util.ResponseUtil;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.OutputStream;
import java.util.Objects;

/**
 * Controller返回值封装
 * @author haiping.huang
 * @date 2020/7/31
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
				&& clazz != Mono.class
				&& clazz != Flux.class
				&& clazz != ResponseEntity.class
				&& clazz != DeferredResult.class;
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType,
								  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
								  ServerHttpRequest request,
                                  ServerHttpResponse response) {

		if (response instanceof ServletServerHttpResponse && ((ServletServerHttpResponse) response).getServletResponse().getStatus() != 200) {
			return body;
		}

		if (body == null) {
			if (String.class.equals(Objects.requireNonNull(returnType.getMethod()).getReturnType())) {
				return JsonUtil.toJSONString(ResponseUtil.ok());
			}
			return ResponseUtil.ok();
		}

		if (body instanceof OutputStream) {
			return body;
		} else if (body instanceof String) {

			response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
			return JsonUtil.toJSONString(ResponseUtil.ok(body));
		} else {
			return ResponseUtil.ok(body);
		}
	}
}
