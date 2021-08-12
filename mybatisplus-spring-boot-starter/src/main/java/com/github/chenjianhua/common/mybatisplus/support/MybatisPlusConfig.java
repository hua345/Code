package com.github.chenjianhua.common.mybatisplus.support;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.github.chenjianhua.common.mybatisplus.config.MybatisPlusAutoProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author chenjianhua
 * @date 2021/4/23
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(MybatisPlusAutoProperties.class)
public class MybatisPlusConfig implements MetaObjectHandler {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(MybatisPlusAutoProperties ktMybatisPlusAutoProperties) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        log.info(ktMybatisPlusAutoProperties.toString());
        /**
         * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题(该属性会在旧插件移除后一同移除)
         */
        PaginationInnerInterceptor innerInterceptor = new PaginationInnerInterceptor(ktMybatisPlusAutoProperties.getDbType());
        innerInterceptor.setOverflow(ktMybatisPlusAutoProperties.getPageConfig().isOverflow());
        innerInterceptor.setMaxLimit(ktMybatisPlusAutoProperties.getPageConfig().getLimit());
        interceptor.addInnerInterceptor(innerInterceptor);

        return interceptor;
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> configuration.setUseDeprecatedExecutor(false);
    }
    /**
     * 重写方法，不管有没有值都覆盖
     */
    @Override
    public MetaObjectHandler strictFillStrategy(MetaObject metaObject, String fieldName, Supplier<?> fieldVal) {
        Object obj = fieldVal.get();
        if (Objects.nonNull(obj)) {
            metaObject.setValue(fieldName, obj);
        }
        return this;
    }
}
