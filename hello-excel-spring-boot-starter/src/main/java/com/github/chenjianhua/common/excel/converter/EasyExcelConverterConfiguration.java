package com.github.chenjianhua.common.excel.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ConverterKeyBuild;
import com.alibaba.excel.converters.DefaultConverterLoader;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author chenjianhua
 * @date 2021/6/29
 */
@Configuration
public class EasyExcelConverterConfiguration {

    @PostConstruct
    public void init() {
        LocalDateStringConverter localDateStringConverter = new LocalDateStringConverter();
        putAllConverter(DefaultConverterLoader.loadAllConverter(), localDateStringConverter);
        putWriteConverter(DefaultConverterLoader.loadDefaultWriteConverter(), localDateStringConverter);

        LocalDateTimeStringConverter localDateTimeStringConverter = new LocalDateTimeStringConverter();
        putAllConverter(DefaultConverterLoader.loadAllConverter(), localDateTimeStringConverter);
        putWriteConverter(DefaultConverterLoader.loadDefaultWriteConverter(), localDateTimeStringConverter);
    }

    private static void putWriteConverter(Map<String, Converter> defaultWriteConverter, Converter converter) {
        defaultWriteConverter.put(ConverterKeyBuild.buildKey(converter.supportJavaTypeKey()), converter);
    }

    private static void putAllConverter(Map<String, Converter> allConverter, Converter converter) {
        allConverter.put(ConverterKeyBuild.buildKey(converter.supportJavaTypeKey(), converter.supportExcelTypeKey()), converter);
    }
}
