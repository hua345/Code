package com.github.springbootjunittest.springboot;

import com.github.springbootjunittest.springboot.beanscan.MyClassPathDefinitionScanner;
import com.github.springbootjunittest.springboot.beanscan.MyScanAnnotation;
import com.github.springbootjunittest.springboot.beanscan.MyScanTestBean;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * @author chenjianhua
 * @date 2021/5/7
 */
@Slf4j
public class BeanScanTest {

    @Test
    void testAnnotationUtils() {
        MyScanAnnotation myScanAnnotation = MyScanTestBean.class.getAnnotation(MyScanAnnotation.class);

        // 注解交给这么一处理  相当于就会被Spring代理了  这就是优势
        MyScanAnnotation myScanAnnotationObj = AnnotationUtils.getAnnotation(myScanAnnotation, MyScanAnnotation.class);
        Assertions.assertNotNull(myScanAnnotationObj);
        System.out.println(myScanAnnotationObj);
    }

    @Test
    public void testSimpleScan() {
        String BASE_PACKAGE = "com.github.springbootjunittest.springboot";
        GenericApplicationContext context = new GenericApplicationContext();
        MyClassPathDefinitionScanner myClassPathDefinitionScanner = new MyClassPathDefinitionScanner(context, MyScanAnnotation.class);
        // 注册过滤器
        myClassPathDefinitionScanner.registerTypeFilter();
        int beanCount = myClassPathDefinitionScanner.scan(BASE_PACKAGE);
        context.refresh();
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        log.info("扫描bean数量:{}", beanCount);
        for (String beanDefinitionName : beanDefinitionNames) {
            log.info(beanDefinitionName);
        }
    }
}
