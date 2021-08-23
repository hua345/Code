package com.github.chenjianhua.common.feign.core;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author chenjianhua
 * @date 2020-09-07 15:41:49
 */
@Slf4j
@Configuration
public class FeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        HttpServletRequest request = null;

        try {
            request =  ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        } catch (Exception e) {
            //log.error("获取上下文请求失败");
        }

        String token = null;
        if (Objects.nonNull(request)) {
            token = request.getHeader(AuthUserContext.getAuthToken());
        }

        if(StringUtils.isEmpty(token)) {
            String tl_token = AuthUserContext.getAuthToken();
            if(StringUtils.hasText(tl_token)) {
                token = tl_token;
            }
        }
        requestTemplate.header(AuthUserContext.AUTH_TOKEN_KEY, token);
    }
}
