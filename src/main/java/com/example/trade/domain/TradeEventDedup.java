package com.example.trade.domain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Data
public class TradeEventDedup {
    private String eventId;
    private String topic;
    private long partitionId;
    private long offsetId;
    private Date consumeTime;
    private String status;

    public static TradeEventDedup of(
            TradeEvent event,
            String topic,
            int partition,
            long offset
    ) {
        TradeEventDedup d = new TradeEventDedup();
        d.setEventId(event.getEventId());
        d.setTopic(topic);
        d.setPartitionId(partition);
        d.setOffsetId(offset);
        d.setStatus("DONE");
        return d;
    }
}
