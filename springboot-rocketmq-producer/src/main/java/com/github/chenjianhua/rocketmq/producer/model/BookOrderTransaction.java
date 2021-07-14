package com.github.chenjianhua.rocketmq.producer.model;

import com.github.chenjianhua.common.mybatisplus.model.AbstractLongModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName book_order_transaction
 */
@Data
public class BookOrderTransaction extends AbstractLongModel {

    /**
     * 订单号
     */
    private String orderNumber;

    /**
     * 事务Id
     */
    private String transactionId;
}