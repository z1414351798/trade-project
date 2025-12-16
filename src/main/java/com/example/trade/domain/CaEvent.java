package com.example.trade.domain;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class CaEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    private String caEventId;
    private String instrumentId;
    private String caType;
    private Date announceDate;
    private Date effectiveDate;
    private BigDecimal param1;
    private BigDecimal param2;
    private String param3;
    private String status;
    private String sourceSystem;
    private Date createTime;
    private Date updateTime;
}
