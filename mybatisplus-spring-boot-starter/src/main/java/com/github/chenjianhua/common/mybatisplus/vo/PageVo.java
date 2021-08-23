package com.github.chenjianhua.common.mybatisplus.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
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
    private long currentPage = 1;
    /**
     * 总页数
     */
    private long totalPage = 1;
    /**
     * 每页记录数，默认为10，最大每页500条
     */
    private int pageSize = 10;
    /**
     * 排序
     */
    private List<SortOrder> orders = new ArrayList<>();
    /**
     * 总记录
     */
    private List<T> rows;

    public void setTotal(long total) {
        if (total < 0) {
            this.total = 0;
        } else {
            this.total = total;
        }
    }

    public void setCurrentPage(int currentPage) {
        if (currentPage <= 0) {
            currentPage = 1;
        }
        this.currentPage = currentPage;
    }

    public long getTotalPage() {
        long n = total % pageSize;
        return n > 0 ? total / pageSize + 1 : total / pageSize;
    }

    public static <T> PageVo<T> of(IPage<T> queryPage) {
        PageVo<T> vo = new PageVo<>();
        vo.setCurrentPage((int) queryPage.getCurrent());
        vo.setRows(queryPage.getRecords());
        vo.setPageSize((int) queryPage.getSize());
        vo.setTotal(queryPage.getTotal());
        return vo;
    }

    public static <T> PageVo<T> of(T result) {
        PageVo<T> vo = new PageVo<>();
        vo.setRows(Collections.singletonList(result));
        vo.setTotal(1);
        vo.setCurrentPage(1);
        vo.setPageSize(10);
        return vo;
    }

    public static <T> PageVo<T> of(List<T> list) {
        PageVo<T> vo = new PageVo<>();
        vo.setRows(list);
        vo.setTotal(list.size());
        vo.setCurrentPage(1);
        vo.setPageSize(list.size());
        return vo;
    }
}
