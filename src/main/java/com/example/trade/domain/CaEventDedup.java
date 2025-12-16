package com.example.trade.domain;
import lombok.Data;
import java.util.Date;

@Data
public class CaEventDedup {
    private String eventId;
    private String topic;
    private long partitionId;
    private long offsetId;
    private Date consumeTime;
    private String status;

    public static CaEventDedup of(
            CaEvent event,
            String topic,
            int partition,
            long offset
    ) {
        CaEventDedup d = new CaEventDedup();
        d.setEventId(event.getCaEventId());
        d.setTopic(topic);
        d.setPartitionId(partition);
        d.setOffsetId(offset);
        d.setStatus("DONE");
        return d;
    }
}
