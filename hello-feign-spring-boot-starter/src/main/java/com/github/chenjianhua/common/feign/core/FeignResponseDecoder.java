package com.github.chenjianhua.common.feign.core;

import com.github.chenjianhua.common.feign.exception.FeignHttpException;
import com.szkunton.common.ktcommon.vo.ResponseStatus;
import com.github.chenjianhua.common.feign.config.FeignAutoProperties;
import com.szkunton.common.ktjson.util.JsonUtils;
import feign.FeignException;
import feign.RequestTemplate;
import feign.Response;
import feign.codec.Decoder;
import feign.optionals.OptionalDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * @author chenjianhua
 * @date 2020-09-07 15:41:49
 */
@Slf4j
public class FeignResponseDecoder extends SpringDecoder {

    final Decoder delegate;

    final FeignAutoProperties feignAutoProperties;

    public FeignResponseDecoder(ObjectFactory<HttpMessageConverters> messageConverters, FeignAutoProperties feignAutoProperties) {
        super(messageConverters);
        this.delegate = new OptionalDecoder(new ResponseEntityDecoder(new SpringDecoder(messageConverters)));
        this.feignAutoProperties = feignAutoProperties;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        if(!feignAutoProperties.getPackDecode()) {
            return response;
        }

        // 判断是否返回参数是否是异常
        String result = IOUtils.toString(response.body().asInputStream(), StandardCharsets.UTF_8.name());
        String rs = null;
        if(StringUtils.hasText(result)) {
            ResponseStatus<?> resp = JsonUtils.toBean(result, ResponseStatus.class);
            //500处理是否抛出异常
            if(resp.getCode() == 500) {
                log.error("调用返回500异常{}", response.reason());
                RequestTemplate requestTemplate = response.request().requestTemplate();
                throw new FeignHttpException("feign调用异常".concat(response.reason()));
            }
            if (resp.isSuccess()) {
                rs = JsonUtils.toJSONString(resp.getData());
            }
        }

        // 拿到返回值，进行自定义逻辑处理
        //log.info("Feign请求结果{}", result);
        // 回写body,因为response的流数据只能读一次，这里回写后重新生成response
        return delegate.decode(response.toBuilder().body(rs, StandardCharsets.UTF_8).build(), type);
    }
}
