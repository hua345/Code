package com.github.chenjianhua.common.json.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;


/**
 * @author chenjianhua
 * @date 2020-09-07 15:41:49
 */
@Slf4j
public class JsonUtil {
    /**
     * https://github.com/FasterXML/jackson-databind
     */
    private static volatile JsonUtil instance = null;
    private static ObjectMapper mapper = null;
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    private JsonUtil() {
        /**
         * https://github.com/FasterXML/jackson-databind#commonly-used-features
         */
        mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.setDateFormat(new SimpleDateFormat(DATE_TIME_PATTERN));
        // 初始化JavaTimeModule
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        //处理LocalDateTime
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter));
        // https://github.com/FasterXML/jackson-modules-java8
        // javaTimeModule注册时间模块, 支持支持jsr310, 即新的时间类(java.time包下的时间类)
        // Jdk8Module模块可以使用java8 Optional
        // ParameterNamesModule可以使用bean构造函数替代注解JsonProperty
        mapper.registerModule(javaTimeModule);
        // 强制将空字符串("")转换为null对象值
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //允许无引号包括的字段
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        //允许单引号
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        //大小写脱敏 默认为false  需要改为true
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, false);
        // 默认小写驼峰命名
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
    }

    public static ObjectMapper getInstance() {
        if (instance == null) {
            synchronized (JsonUtil.class) {
                if (instance == null) {
                    instance = new JsonUtil();
                }
            }
        }
        return mapper;
    }

    /**
     * 将java对象转换成json字符串
     */
    public static String toJsonString(Object obj) {
        try {
            ObjectMapper objectMapper = getInstance();
            String json = objectMapper.writeValueAsString(obj);
            return json;
        } catch (Exception e) {
            log.error("JsonUtil toJSONString error", e);
        }
        return null;
    }

    /**
     * 将java对象转换成json字符串
     */
    public static String toPrettyJsonString(Object obj) {
        try {
            ObjectMapper objectMapper = getInstance();
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            return json;
        } catch (Exception e) {
            log.error("JsonUtil toJSONString error", e);
        }
        return null;
    }

    /**
     * 将java对象转换成json字符串
     */
    public static byte[] toByte(Object obj) {
        try {
            ObjectMapper objectMapper = getInstance();
            byte[] json = objectMapper.writeValueAsBytes(obj);
            return json;
        } catch (Exception e) {
            log.error("JsonUtil toByte error", e);
        }
        return null;
    }

    /**
     * 将json字符串转换成java对象
     */
    public static <T> T toBean(String json, Class<T> cls) {
        try {
            ObjectMapper objectMapper = getInstance();
            T vo = objectMapper.readValue(json, cls);
            return vo;
        } catch (Exception e) {
            log.error("JsonUtil String toBean error Json: {}", json, e);
        }
        return null;
    }

    public static <T> T toBean(InputStream src, Class<T> cls) {
        try {
            ObjectMapper objectMapper = getInstance();
            T vo = objectMapper.readValue(src, cls);
            return vo;
        } catch (Exception e) {
            log.error("JsonUtil String toBean error Json: {}", src, e);
        }
        return null;
    }

    public static <T> T toBean(byte[] bytes, Class<T> cls) {
        try {
            ObjectMapper objectMapper = getInstance();
            T vo = objectMapper.readValue(bytes, cls);
            return vo;
        } catch (Exception e) {
            log.error("byte to bean error.", e);
        }
        return null;
    }

    /**
     * 将json字符串转换成List java对象
     */
    public static <T> T toList(String json, Class<?> cls) {
        try {
            getInstance();
            JavaType javaType = getCollectionType(ArrayList.class, cls);
            return mapper.readValue(json, javaType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取泛型的Collection Type
     */
    @SuppressWarnings("rawtypes")
    public static JavaType getCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClasses) {
        return mapper.getTypeFactory().constructCollectionType(collectionClass, elementClasses);
    }

    public static void main(String[] args) {
        class TestA {
            public LocalDateTime localDateTime;
            public LocalDate localDate;
        }
        TestA testA = new TestA();
        testA.localDateTime = LocalDateTime.now();
        testA.localDate = LocalDate.now();
        log.info("localDateTime测试:{}", JsonUtil.toJsonString(testA));
    }
}
