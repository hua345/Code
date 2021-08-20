package com.github.chenjianhua.common.mybatisplus.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.*;

/**
 * @author chenjianhua
 * @date 2021/4/23
 */
@Getter
@Setter
@ToString
public class PageVo<T> implements Serializable {
    /**
     * 总记录数
     */
    private long total;
    /**
     * 当前页
     */
    private long page = 1;

    private long totalPage = 1;
    /**
     * 每页记录数，默认为10，最大每页500条
     */
    private int size = 10;
    /**
     * 排序
     */
    private List<SortOrder> orders = new ArrayList<>();
    /**
     * 总记录
     */
    private List<T> rows;

    public void setTotal(long total) {
        this.total = total;
        long n = total % size;
        page = n > 0 ? total / size + 1 : total / size;
    }

    public void setPage(int page) {
        if (page <= 0) {
            page = 1;
        }
        this.page = page;
    }

    public long getTotalPage() {
        long n = total % size;
        return n > 0 ? total / size + 1 : total / size;
    }

    public static <T> PageVo<T> of(IPage<T> queryPage) {
        PageVo<T> vo = new PageVo<>();
        vo.setPage((int) queryPage.getCurrent());
        vo.setRows(queryPage.getRecords());
        vo.setSize((int) queryPage.getSize());
        vo.setTotal(queryPage.getTotal());
        return vo;
    }

    public static <T> PageVo<T> of(T result) {
        PageVo<T> vo = new PageVo<>();
        vo.setRows(Collections.singletonList(result));
        vo.setTotal(1);
        vo.setPage(1);
        vo.setSize(10);
        return vo;
    }

    public static <T> PageVo<T> of(T result, int pageSize) {
        PageVo<T> vo = new PageVo<>();
        vo.setRows(Collections.singletonList(result));
        vo.setTotal(1);
        vo.setPage(1);
        vo.setSize(pageSize);
        return vo;
    }

    public static <T> PageVo<T> of(List<T> results, int pageSize) {
        PageVo<T> vo = new PageVo<>();
        vo.setRows(results);
        vo.setTotal(1);
        vo.setPage(1);
        vo.setSize(pageSize);
        return vo;
    }
}
