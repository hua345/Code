package com.github.springbootjunittest.springboot.beanscan;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenjianhua
 * @date 2021/5/7
 */
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        //获取所有注解的属性和值
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableMyScanAnnotation.class.getName()));
        String basePackage = attributes.getString("scanPath");
        if (!StringUtils.hasText(basePackage)) {
            basePackage = ((StandardAnnotationMetadata) importingClassMetadata).getIntrospectedClass().getPackage().getName();
        }
        String defaultPath = getDefaultBasePackage(importingClassMetadata);
        MyClassPathDefinitionScanner scanner = new MyClassPathDefinitionScanner(registry, MyScanAnnotation.class);
        scanner.setResourceLoader(resourceLoader);
        scanner.registerTypeFilter();
        scanner.doScan(basePackage);

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MyBeanDefinitionRegistryPostProcessor.class);
        List<String> basePackages = new ArrayList();
        basePackages.add(getDefaultBasePackage(importingClassMetadata));

        builder.addPropertyValue("basePackage", StringUtils.collectionToCommaDelimitedString(basePackages));
        registry.registerBeanDefinition(generateBaseBeanName(importingClassMetadata, 0), builder.getBeanDefinition());
    }

    private static String generateBaseBeanName(AnnotationMetadata importingClassMetadata, int index) {
        return importingClassMetadata.getClassName() + "#" + MyImportBeanDefinitionRegistrar.class.getSimpleName() + "#" + index;
    }

    private static String getDefaultBasePackage(AnnotationMetadata importingClassMetadata) {
        return ClassUtils.getPackageName(importingClassMetadata.getClassName());
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}