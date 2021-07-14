package com.github.springbootjunittest.java.template;

import com.github.chenjianhua.common.json.util.JsonUtil;
import com.github.springbootjunittest.java.reflect.TableFieldInfoBo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.ResolvableType;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenjianhua
 * @date 2021/6/15
 */
@Slf4j
public class ResolvableTypeTest {
    @Test
    public void testResolvableType() {
        // 获取父类型
        ResolvableType superType = ResolvableType.forClass(Student.class).getSuperType();
        // 获取泛型类型
        ResolvableType[] superGenericsType = superType.getGenerics();
        Assertions.assertEquals(superGenericsType[0].resolve(), String.class);
        Assertions.assertEquals(superGenericsType[1].resolve(), Integer.class);
    }

    @Test
    public void testFieldResolvableType() {
        Field field = ReflectionUtils.findField(Student.class, "tableFieldInfoBos");

        ResolvableType resolvableType = ResolvableType.forField(field);
        Assertions.assertEquals(List.class, resolvableType.resolve());

        Class<?> clazz = resolvableType.getGeneric(0).resolve();
        Assertions.assertEquals(TableFieldInfoBo.class, clazz);
    }

    private Student initTestStudent() {
        Student student = new Student();
        List<TableFieldInfoBo> aa = new ArrayList<>(8);
        TableFieldInfoBo tableFieldInfoBo = new TableFieldInfoBo();
        tableFieldInfoBo.setFieldCode("bookName");
        tableFieldInfoBo.setFieldName("图书名");
        aa.add(tableFieldInfoBo);
        student.setTableFieldInfoBos(aa);
        return student;
    }

    @Test
    public void testFieldResolvableType2() {
        Student student = initTestStudent();
        Field[] declaredFields = student.getClass().getDeclaredFields();
        Field dynamicTableField = null;
        for (Field field : declaredFields) {
            ResolvableType resolvableType = ResolvableType.forField(field);
            if (List.class == resolvableType.resolve()) {
                Class<?> clazz = resolvableType.getGeneric(0).resolve();
                if (TableFieldInfoBo.class == clazz) {
                    dynamicTableField = field;
                    break;
                }
            }
        }
        List<TableFieldInfoBo> tableFieldInfoBos = null;
        if (null != dynamicTableField) {
            dynamicTableField.setAccessible(true);
            try {
                tableFieldInfoBos = (List<TableFieldInfoBo>) dynamicTableField.get(student);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        log.info("{}", JsonUtil.toJsonString(tableFieldInfoBos));
    }

    @Test
    public void testFieldAnnotation() {
        Student student = initTestStudent();
        Field[] declaredFields = student.getClass().getDeclaredFields();
        Field dynamicTableField = null;
        for (Field field : declaredFields) {
            FieldDescAnnotion fieldDescAnnotion = field.getAnnotation(FieldDescAnnotion.class);
            if (null != fieldDescAnnotion) {
                dynamicTableField = field;
                break;
            }
        }
        List<TableFieldInfoBo> tableFieldInfoBos = null;
        if (null != dynamicTableField) {
            dynamicTableField.setAccessible(true);
            try {
                tableFieldInfoBos = (List<TableFieldInfoBo>) dynamicTableField.get(student);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        log.info("{}", JsonUtil.toJsonString(tableFieldInfoBos));
    }
}
