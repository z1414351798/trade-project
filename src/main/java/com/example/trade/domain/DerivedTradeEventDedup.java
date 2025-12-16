package com.example.trade.domain;
import lombok.Data;

import java.util.Date;

@Data
public class DerivedTradeEventDedup {
    private String eventId;
    private String topic;
    private long partitionId;
    private long offsetId;
    private Date consumeTime;
    private String status;

    public static DerivedTradeEventDedup of(
            DerivedTradeEvent event,
            String topic,
            int partition,
            long offset
    ) {
        DerivedTradeEventDedup d = new DerivedTradeEventDedup();
        d.setEventId(event.getDerivedTradeId());
        d.setTopic(topic);
        d.setPartitionId(partition);
        d.setOffsetId(offset);
        d.setStatus("DONE");
        return d;
    }
}
