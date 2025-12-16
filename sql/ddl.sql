create database aspen;
use aspen;

CREATE TABLE trade_event (
    event_id        VARCHAR(50)  NOT NULL,
    trade_id        VARCHAR(50)  NOT NULL,
    account_id      VARCHAR(50)  NOT NULL,
    instrument_id   VARCHAR(50)  NOT NULL,
    event_type      VARCHAR(30)  NOT NULL,
    quantity        DECIMAL(18,6),
    price           DECIMAL(18,6),
    amount          DECIMAL(18,2),
    currency        VARCHAR(10),
    status          VARCHAR(20),
    source_system   VARCHAR(50),
    trade_date      DATETIME     NOT NULL,
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
                     ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (event_id),
    KEY idx_te_trade (trade_id),
    KEY idx_te_symbol (instrument_id),
    KEY idx_te_tradedate (trade_date)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4;

CREATE TABLE ca_event (
    ca_event_id     VARCHAR(50) NOT NULL,
    instrument_id   VARCHAR(50) NOT NULL,
    ca_type         VARCHAR(30) NOT NULL,
    announce_date   DATETIME,
    effective_date  DATETIME    NOT NULL,
    param1          DECIMAL(18,6),
    param2          DECIMAL(18,6),
    param3          VARCHAR(100),
    status          VARCHAR(20),
    source_system   VARCHAR(50),
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                     ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ca_event_id),
    KEY idx_ca_symbol (instrument_id),
    KEY idx_ca_effective (effective_date)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4;

CREATE TABLE derived_trade (
    derived_trade_id   VARCHAR(50) NOT NULL,
    source_trade_id    VARCHAR(50) NOT NULL,
    ca_event_id        VARCHAR(50) NOT NULL,
    account_id         VARCHAR(50) NOT NULL,
    instrument_id      VARCHAR(50) NOT NULL,
    new_instrument_id  VARCHAR(50),
    original_qty       DECIMAL(18,6),
    adjusted_qty       DECIMAL(18,6),
    original_price     DECIMAL(18,6),
    adjusted_price     DECIMAL(18,6),
    effective_date     DATETIME    NOT NULL,
    ca_type            VARCHAR(30) NOT NULL,
    create_time        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                         ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (derived_trade_id),
    KEY idx_dt_source_trade (source_trade_id),
    KEY idx_dt_ca_event (ca_event_id),
    KEY idx_dt_symbol (instrument_id),
    KEY idx_dt_effective (effective_date)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4;

CREATE TABLE trade_event_dedup (
    event_id      VARCHAR(64) NOT NULL,
    topic         VARCHAR(100),
    partition_id  INT,
    offset_id     BIGINT,
    consume_time  DATETIME,
    status        VARCHAR(20),
    PRIMARY KEY (event_id),
    KEY idx_ted_consume_time (consume_time)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4;

CREATE TABLE ca_event_dedup (
    event_id      VARCHAR(64) NOT NULL,
    topic         VARCHAR(100),
    partition_id  INT,
    offset_id     BIGINT,
    consume_time  DATETIME,
    status        VARCHAR(20),
    PRIMARY KEY (event_id),
    KEY idx_ced_consume_time (consume_time)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4;

CREATE TABLE derived_trade_event_dedup (
    event_id      VARCHAR(64) NOT NULL,
    topic         VARCHAR(100),
    partition_id  INT,
    offset_id     BIGINT,
    consume_time  DATETIME,
    status        VARCHAR(20),
    PRIMARY KEY (event_id),
    KEY idx_ced_consume_time (consume_time)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4;

use aspen;
select * from trade_event;
select * from TRADE_EVENT;
select * from TRADE_EVENT_DEDUP;
select * from trade_event_dedup;
select * from ca_event;


CREATE TABLE trade_aggregate (
    trade_id            BIGINT       NOT NULL COMMENT '交易唯一ID',

    settlement_status   VARCHAR(16)   NOT NULL DEFAULT 'INIT',
    position_status     VARCHAR(16)   NOT NULL DEFAULT 'INIT',
    announcement_status VARCHAR(16)   NOT NULL DEFAULT 'INIT',
    alert_status        VARCHAR(16)   NOT NULL DEFAULT 'INIT',
    notification_status VARCHAR(16)   NOT NULL DEFAULT 'INIT',

    final_status        VARCHAR(16)   NOT NULL DEFAULT 'PROCESSING',
    version             INT           NOT NULL DEFAULT 0,

    create_time         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (trade_id),

    KEY idx_final_status (final_status),
    KEY idx_update_time (update_time)
) ENGINE=InnoDB COMMENT='交易下游处理状态聚合表';

CREATE TABLE settlement (
    settlement_id   BIGINT       AUTO_INCREMENT PRIMARY KEY,
    trade_id        BIGINT       NOT NULL,

    amount          DECIMAL(18,2) NOT NULL,
    currency        VARCHAR(10)   NOT NULL,

    status          VARCHAR(16)   NOT NULL,
    error_code      VARCHAR(64),
    error_message   VARCHAR(255),

    create_time     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uk_trade (trade_id),
    KEY idx_status (status)
) ENGINE=InnoDB COMMENT='交易清算表';

CREATE TABLE position (
    position_id     BIGINT      AUTO_INCREMENT PRIMARY KEY,
    trade_id        BIGINT      NOT NULL,

    account_id      VARCHAR(64) NOT NULL,
    instrument_id   VARCHAR(64) NOT NULL,
    quantity        DECIMAL(18,6) NOT NULL,

    status          VARCHAR(16) NOT NULL,

    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uk_trade (trade_id),
    KEY idx_account (account_id)
) ENGINE=InnoDB COMMENT='持仓变更表';

CREATE TABLE announcement (
    announcement_id BIGINT      AUTO_INCREMENT PRIMARY KEY,
    trade_id        BIGINT      NOT NULL,

    channel         VARCHAR(32) NOT NULL COMMENT 'EMAIL/SYSTEM',
    content         TEXT        NOT NULL,

    status          VARCHAR(16) NOT NULL,
    retry_count     INT         NOT NULL DEFAULT 0,

    create_time     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uk_trade (trade_id),
    KEY idx_status (status)
) ENGINE=InnoDB COMMENT='交易公告表';

CREATE TABLE alert (
    alert_id        BIGINT      AUTO_INCREMENT PRIMARY KEY,
    trade_id        BIGINT      NOT NULL,

    alert_type      VARCHAR(32) NOT NULL,
    severity        VARCHAR(16) NOT NULL COMMENT 'LOW/MEDIUM/HIGH',
    message         VARCHAR(255),

    status          VARCHAR(16) NOT NULL,

    create_time     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    KEY idx_trade (trade_id),
    KEY idx_severity (severity)
) ENGINE=InnoDB COMMENT='交易告警表';

CREATE TABLE notification (
    notification_id BIGINT      AUTO_INCREMENT PRIMARY KEY,
    trade_id        BIGINT      NOT NULL,

    target          VARCHAR(64) NOT NULL COMMENT 'userId / system',
    channel         VARCHAR(32) NOT NULL COMMENT 'SMS/PUSH/EMAIL',

    status          VARCHAR(16) NOT NULL,
    retry_count     INT         NOT NULL DEFAULT 0,

    create_time     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uk_trade_channel (trade_id, channel),
    KEY idx_status (status)
) ENGINE=InnoDB COMMENT='交易通知表';

CREATE TABLE settlement_event_dedup (
    event_id      VARCHAR(64) NOT NULL,
    topic         VARCHAR(100),
    partition_id  INT,
    offset_id     BIGINT,
    consume_time  DATETIME,
    status        VARCHAR(20),
    PRIMARY KEY (event_id),
    KEY idx_ced_consume_time (consume_time)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4;

CREATE TABLE position_event_dedup (
    event_id      VARCHAR(64) NOT NULL,
    topic         VARCHAR(100),
    partition_id  INT,
    offset_id     BIGINT,
    consume_time  DATETIME,
    status        VARCHAR(20),
    PRIMARY KEY (event_id),
    KEY idx_ced_consume_time (consume_time)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4;

CREATE TABLE announcement_event_dedup (
    event_id      VARCHAR(64) NOT NULL,
    topic         VARCHAR(100),
    partition_id  INT,
    offset_id     BIGINT,
    consume_time  DATETIME,
    status        VARCHAR(20),
    PRIMARY KEY (event_id),
    KEY idx_ced_consume_time (consume_time)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4;

CREATE TABLE alert_event_dedup (
    event_id      VARCHAR(64) NOT NULL,
    topic         VARCHAR(100),
    partition_id  INT,
    offset_id     BIGINT,
    consume_time  DATETIME,
    status        VARCHAR(20),
    PRIMARY KEY (event_id),
    KEY idx_ced_consume_time (consume_time)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4;

CREATE TABLE notification_event_dedup (
    event_id      VARCHAR(64) NOT NULL,
    topic         VARCHAR(100),
    partition_id  INT,
    offset_id     BIGINT,
    consume_time  DATETIME,
    status        VARCHAR(20),
    PRIMARY KEY (event_id),
    KEY idx_ced_consume_time (consume_time)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4;

