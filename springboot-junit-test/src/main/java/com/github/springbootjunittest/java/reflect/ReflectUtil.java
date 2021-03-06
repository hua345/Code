package com.github.springbootjunittest.java.reflect;

import com.github.chenjianhua.common.json.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * @author chenjianhua
 * @date 2021/3/16
 */
@Slf4j
public class ReflectUtil {
    public static Object getObjectValue(Object obj, TableFieldInfoBo fieldItem) {
        Object value = null;
        try {
            Field field = obj.getClass().getDeclaredField(fieldItem.getFieldCode());
            field.setAccessible(true);
            value = field.get(obj);
        } catch (NoSuchFieldException | IllegalArgumentException e) {
            log.error("没有这个字段信息:{}", fieldItem.getFieldCode());
        } catch (IllegalAccessException e) {
            log.error("没有这个字段访问权限:{}", fieldItem.getFieldCode());
        }
        return value;
    }

    public static List<Object> getClassFieldValue(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        List<Object> rowData = new LinkedList<>();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                rowData.add(field.get(obj));
            }
        } catch (Exception e) {
            log.error("获取类字段信息失败:{}", JsonUtil.toJsonString(e));
        }
        return rowData;
    }

    public static void main(String[] args) {
        TableFieldInfoBo tableFieldInfoBo = new TableFieldInfoBo();
        tableFieldInfoBo.setFieldCode("name");
        tableFieldInfoBo.setFieldName("芳");
        tableFieldInfoBo.setName("芳芳");
        log.info(JsonUtil.toJsonString(ReflectUtil.getObjectValue(tableFieldInfoBo, tableFieldInfoBo)));
        log.info(JsonUtil.toJsonString(ReflectUtil.getClassFieldValue(tableFieldInfoBo)));
    }
}
