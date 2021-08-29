package com.github.chenjianhua.common.excel.util;

import com.github.chenjianhua.common.excel.entity.BeginAndEndTimeBo;
import com.github.chenjianhua.common.excel.entity.PageSplitBo;
import com.github.chenjianhua.common.json.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author chenjianhua
 * @date 2020/12/22
 */
@Slf4j
public class ExcelSplitUtil {
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    public static final Integer MIN_UNIT_NUM = 3000;

    public static final Integer DEFAULT_PAGE_SIZE = 3000;

    public static final BigDecimal MIN_UNIT_BIG_DECIMAL = BigDecimal.valueOf(MIN_UNIT_NUM);

    /**
     * 最小分割时间段为1天
     */
    public static List<BeginAndEndTimeBo> splitByDay(Long exportCount, LocalDateTime startTradeTime, LocalDateTime endTradeTime) {
        return splitTime(exportCount, startTradeTime, endTradeTime, ChronoUnit.DAYS);
    }

    /**
     * 最小分割时间段为1小时
     */
    public static List<BeginAndEndTimeBo> splitByHour(Long exportCount, LocalDateTime startTradeTime, LocalDateTime endTradeTime) {
        return splitTime(exportCount, startTradeTime, endTradeTime, ChronoUnit.HOURS);
    }

    /**
     * 最小分割时间段为1分钟
     */
    public static List<BeginAndEndTimeBo> splitByMinute(Long exportCount, LocalDateTime startTradeTime, LocalDateTime endTradeTime) {
        return splitTime(exportCount, startTradeTime, endTradeTime, ChronoUnit.MINUTES);
    }

    private static List<BeginAndEndTimeBo> splitLocalDateTime(LocalDateTime startLocalDateTime, LocalDateTime endLocalDateTime, int step, int splitNum, ChronoUnit chronoUnit) {
        List<BeginAndEndTimeBo> beginAndEndTimeBoList = new ArrayList<>(splitNum);
        for (int i = 0; i < splitNum; i++) {
            BeginAndEndTimeBo beginAndEndTimeBo = new BeginAndEndTimeBo();
            beginAndEndTimeBo.setStartLocalDateTime(startLocalDateTime);
            if (ChronoUnit.DAYS.equals(chronoUnit)) {
                beginAndEndTimeBo.setEndLocalDateTime(startLocalDateTime.plusDays(step));
            } else if (ChronoUnit.HOURS.equals(chronoUnit)) {
                beginAndEndTimeBo.setEndLocalDateTime(startLocalDateTime.plusHours(step));
            } else if (ChronoUnit.MINUTES.equals(chronoUnit)) {
                beginAndEndTimeBo.setEndLocalDateTime(startLocalDateTime.plusMinutes(step));
            }
            startLocalDateTime = beginAndEndTimeBo.getEndLocalDateTime();
            if (beginAndEndTimeBo.getEndLocalDateTime().isBefore(endLocalDateTime)) {
                // -1秒为了防止，0点 >= <= 时间 导致的重复问题
                beginAndEndTimeBo.setEndLocalDateTime(beginAndEndTimeBo.getEndLocalDateTime().plusSeconds(-1));
                beginAndEndTimeBoList.add(beginAndEndTimeBo);
            } else {
                beginAndEndTimeBo.setEndLocalDateTime(endLocalDateTime);
                beginAndEndTimeBoList.add(beginAndEndTimeBo);
                break;
            }
        }
        return beginAndEndTimeBoList;
    }

    private static List<BeginAndEndTimeBo> splitTime(Long exportCount, LocalDateTime startTradeTime, LocalDateTime endTradeTime, ChronoUnit chronoUnit) {
        if (Objects.isNull(endTradeTime)) {
            endTradeTime = LocalDateTime.now();
        }
        //  开始时间service层做了判断，一定是有值的
        int splitNum = new BigDecimal(exportCount).divide(MIN_UNIT_BIG_DECIMAL, RoundingMode.UP).intValue();
        splitNum = splitNum + 1;
        // 实际的结束实际会带时分秒，比ChronoUnit.DAYS.between多23:59:59
        long totalTimes = 0L;
        if (ChronoUnit.DAYS.equals(chronoUnit)) {
            totalTimes = ChronoUnit.DAYS.between(startTradeTime, endTradeTime) + 1;
        } else if (ChronoUnit.HOURS.equals(chronoUnit)) {
            totalTimes = ChronoUnit.HOURS.between(startTradeTime, endTradeTime) + 1;
        } else if (ChronoUnit.MINUTES.equals(chronoUnit)) {
            totalTimes = ChronoUnit.MINUTES.between(startTradeTime, endTradeTime) + 1;
        }
        // 时间分割步长
        int step = new BigDecimal(totalTimes).divide(BigDecimal.valueOf(splitNum), RoundingMode.UP).intValue();
        if (step < 1) {
            step = 1;
        }
        List<BeginAndEndTimeBo> beginAndEndTimeBoList = splitLocalDateTime(startTradeTime, endTradeTime, step, splitNum, chronoUnit);
        if (ChronoUnit.DAYS.equals(chronoUnit)) {
            log.info("导出时间范围:{} ~ {},exportCount:{},分割次数:{},时间段大小:{},总天数:{},间隔天数:{}", startTradeTime.format(DATE_TIME_FORMATTER), endTradeTime.format(DATE_TIME_FORMATTER), exportCount, splitNum, beginAndEndTimeBoList.size(), totalTimes, step);
        } else if (ChronoUnit.HOURS.equals(chronoUnit)) {
            log.info("导出时间范围:{} ~ {},exportCount:{},分割次数:{},时间段大小:{},总小时:{},间隔小时:{}", startTradeTime.format(DATE_TIME_FORMATTER), endTradeTime.format(DATE_TIME_FORMATTER), exportCount, splitNum, beginAndEndTimeBoList.size(), totalTimes, step);
        } else if (ChronoUnit.MINUTES.equals(chronoUnit)) {
            log.info("导出时间范围:{} ~ {},exportCount:{},分割次数:{},时间段大小:{},总分钟:{},间隔分钟:{}", startTradeTime.format(DATE_TIME_FORMATTER), endTradeTime.format(DATE_TIME_FORMATTER), exportCount, splitNum, beginAndEndTimeBoList.size(), totalTimes, step);
        }
        return beginAndEndTimeBoList;
    }

    /**
     * 对总数进行分页,默认1000
     */
    public static List<PageSplitBo> splitPage(long totalSize) {
        return splitPage(totalSize, DEFAULT_PAGE_SIZE);
    }

    /**
     * 对总数进行分页
     */
    public static List<PageSplitBo> splitPage(long totalSize, int pageSize) {
        List<PageSplitBo> pageSplitBos = new LinkedList<>();
        if (pageSize >= totalSize) {
            PageSplitBo pageSplitBo = new PageSplitBo();
            pageSplitBo.setPage(1);
            pageSplitBo.setPageSize(pageSize);
            pageSplitBos.add(pageSplitBo);
        } else {
            for (int page = 0; page * pageSize < totalSize; page++) {
                PageSplitBo pageSplitBo = new PageSplitBo();
                pageSplitBo.setPage(page + 1);
                pageSplitBo.setPageSize(pageSize);
                pageSplitBos.add(pageSplitBo);
            }
        }
        return pageSplitBos;
    }

    public static void main(String[] args) {
        LocalDateTime rangeDate = LocalDateTime.now().plusMonths(-1).plusDays(-1);
        log.info("rangeDate: {}", rangeDate.format(DATE_TIME_FORMATTER));
        LocalDateTime startLocalDateTime = LocalDateTime.of(2020, 12, 01, 00, 00, 00);
        LocalDateTime endLocalDateTime = LocalDateTime.of(2020, 12, 30, 23, 59, 59);
        List<BeginAndEndTimeBo> beginAndEndTimeBos = splitByDay(16 * 1000L, startLocalDateTime, endLocalDateTime);
        beginAndEndTimeBos.forEach(item -> log.info(JsonUtil.toJsonString(item)));
        beginAndEndTimeBos = splitByDay(16 * 1000L, startLocalDateTime, null);
        beginAndEndTimeBos.forEach(item -> log.info(JsonUtil.toJsonString(item)));

        beginAndEndTimeBos = splitByHour(16 * 1000L, startLocalDateTime, endLocalDateTime);
        beginAndEndTimeBos.forEach(item -> log.info(JsonUtil.toJsonString(item)));
        beginAndEndTimeBos = splitByHour(16 * 1000L, startLocalDateTime, null);
        beginAndEndTimeBos.forEach(item -> log.info(JsonUtil.toJsonString(item)));

        List<PageSplitBo> pageSplitBos = splitPage(160, 20);
        pageSplitBos.forEach(item -> log.info(JsonUtil.toJsonString(item)));
        pageSplitBos = splitPage(162, 20);
        pageSplitBos.forEach(item -> log.info(JsonUtil.toJsonString(item)));
        pageSplitBos = splitPage(2, 20);
        pageSplitBos.forEach(item -> log.info(JsonUtil.toJsonString(item)));
    }
}
