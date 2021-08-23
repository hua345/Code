package com.github.chenjianhua.common.excel.support.http;

import com.github.chenjianhua.common.excel.enums.LoginInfoConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @author chenjianhua
 * @date 2021/4/16
 */
@Slf4j
@Component
public class RestTemplateExcelService {
    private RestTemplate restTemplate;

    private RestTemplateExcelService() {

    }

    public RestTemplateExcelService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public static ResponseEntity<String> getRequest(String url) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForEntity(url, String.class);
    }

    public static ResponseEntity<String> postJson(String url, String jsonStr, String authToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(LoginInfoConstant.AUTH_TOKEN_KEY, authToken);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        HttpEntity<String> request = new HttpEntity<>(jsonStr, headers);
        return restTemplate.postForEntity(url, request, String.class);
    }

    public ResponseEntity<String> postJsonByServerName(String serverName, String uri, String jsonStr, String authToken) {
        if (!StringUtils.hasText(serverName)) {
            throw new RuntimeException("导出服务名没有配置");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("http://").append(serverName).append("/").append(uri);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(LoginInfoConstant.AUTH_TOKEN_KEY, authToken);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        HttpEntity<String> request = new HttpEntity<>(jsonStr, headers);
        return restTemplate.postForEntity(sb.toString(), request, String.class);
    }
}
