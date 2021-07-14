package com.github.springbootjunittest.java.template;

import com.github.springbootjunittest.java.reflect.TableFieldInfoBo;
import lombok.Data;

import java.util.List;

/**
 * @author chenjianhua
 * @date 2021/5/8
 */
@Data
public class Student extends Person<String, Integer> {

    @FieldDescAnnotion
    List<TableFieldInfoBo> tableFieldInfoBos;
}
