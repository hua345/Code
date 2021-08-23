package com.github.chenjianhua.common.mybatisplus.support;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.chenjianhua.common.mybatisplus.vo.PageVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author chenjianhua
 * @date 2021/4/23
 */
@Slf4j
public abstract class AbstractService<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    /**
     * 转换vo
     *
     * @param t     source
     * @param clazz target class
     * @param <R>   R
     * @return R
     */
    public <R> R convert(T t, Class<R> clazz) {
        if (Objects.isNull(t)) {
            return null;
        }
        R instance;
        try {
            instance = clazz.newInstance();
        } catch (Exception e) {
            log.error("instance cast error!");
            throw new RuntimeException("instance cast error!");
        }
        BeanUtils.copyProperties(t, instance);
        return instance;
    }

    /**
     * 自动转换映射vo
     *
     * @param collection 待转换集合
     * @param clazz      vo.class
     * @param <R>        R
     * @return List<R>
     */
    public <R> List<R> convert(Collection<T> collection, Class<R> clazz) {
        if (CollectionUtils.isEmpty(collection)) {
            return Collections.emptyList();
        }
        return collection.stream().map(v -> convert(v, clazz)).collect(Collectors.toList());
    }

    /**
     * 重载方法,对空集合做返回空处理
     *
     * @param idList ids
     * @return List<T>
     */
    @Override
    public List<T> listByIds(Collection<? extends Serializable> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return Collections.emptyList();
        }
        return super.listByIds(idList);
    }

    /**
     * 将iPage转化为PageVo,并且转换vo类型
     *
     * @param iPage iPage
     * @param clazz vo
     * @param <E>   E
     * @return 将iPage转化为PageVo
     */
    public <E> PageVo<E> convertPage(IPage<T> iPage, Class<E> clazz) {
        PageVo<E> pageVo = new PageVo<>();
        pageVo.setRows(convert(iPage.getRecords(), clazz));
        pageVo.setCurrentPage((int) iPage.getCurrent());
        pageVo.setPageSize((int) iPage.getSize());
        pageVo.setTotal((int) iPage.getTotal());
        return pageVo;
    }

    /**
     * 将iPage转化为PageVo
     *
     * @param iPage iPage
     * @param <E>   E
     * @return 将iPage转化为PageVo
     */
    public <E> PageVo<E> convertPage(IPage<E> iPage) {
        PageVo<E> pageVo = new PageVo<>();
        pageVo.setRows(iPage.getRecords());
        pageVo.setCurrentPage((int) iPage.getCurrent());
        pageVo.setPageSize((int) iPage.getSize());
        pageVo.setTotal((int) iPage.getTotal());
        return pageVo;
    }
}