package com.github.chenjianhua.common.excel.support.eventlistener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.github.chenjianhua.common.excel.util.ApplicationContextUtil;
import com.github.common.config.exception.BusinessException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
@Slf4j
@Getter
public abstract class AbstractKtAnalysisEventListener<T> extends AnalysisEventListener<T> {
    /**
     * 导入成功条数
     */
    protected Long successRecord = 0L;
    /**
     * 导入失败条数
     */
    protected Long failedRecord = 0L;
    /**
     * 读取的数据列表
     */
    protected final List<T> allReadRows = new LinkedList<>();
    /**
     *
     */
    protected boolean batchStatus;
    /**
     * 导出结果文件
     */
    protected File resultTempFile;

    private final SmartValidator validator = ApplicationContextUtil.getBean(SmartValidator.class);

    /**
     * 导入Model springboot校验注解检查
     */
    protected void checkBindingResult(T rowData) {
        BindingResult bindingResult = new BeanPropertyBindingResult(rowData, rowData.getClass().getSimpleName());
        if (null == validator) {
            return;
        }
        validator.validate(rowData, bindingResult);
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(";"));
            throw new BusinessException(message);
        }
    }

    /**
     * 这里会一行行的返回头
     *
     * @param headMap excel头部信息
     * @param context excel上下文
     */
    @Override
    public abstract void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context);

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param rowData one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context excel上下文
     */
    @Override
    public abstract void invoke(T rowData, AnalysisContext context);

    /**
     * 需要实现的处理行数据方法
     *
     * @param t       模型数据
     * @param context excel上下文
     */
    public abstract void processData(T t, AnalysisContext context);

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context excel上下文
     */
    @Override
    public abstract void doAfterAllAnalysed(AnalysisContext context);

    /**
     * 如果需要所有数据检查完后再入库，需要实现的方法
     *
     * @param rowDatas 检查所有数据都成功后返回的所有数据
     */
    public void doAfterAllProcessData(List<T> rowDatas) {
    }
}
