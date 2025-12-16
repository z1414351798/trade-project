package com.example.trade.domain;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class DerivedTradeEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    private String derivedTradeId;
    private String sourceTradeId;
    private String caEventId;
    private String accountId;
    private String instrumentId;
    private String newInstrumentId;
    private BigDecimal originalQty;
    private BigDecimal adjustedQty;
    private BigDecimal originalPrice;
    private BigDecimal adjustedPrice;
    private Date effectiveDate;
    private String caType;
    private Date createTime;
    private Date updateTime;
}
