package com.example.trade.domain;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class TradeEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    private String eventId;
    private String tradeId;
    private String accountId;
    private String instrumentId;
    private String eventType;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String sourceSystem;
    private Date tradeDate;
    private Date createTime;
    private Date updateTime;
}
