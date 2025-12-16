//package com.example.trade.config;
//
//import com.example.trade.domain.CaEvent;
//import com.example.trade.domain.DerivedTradeEvent;
//import com.example.trade.domain.TradeEvent;
//import org.apache.kafka.common.serialization.Serdes;
//import org.apache.kafka.streams.kstream.*;
//import org.springframework.kafka.support.serializer.JsonSerde;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.apache.kafka.streams.StreamsBuilder;
//
//import java.time.Duration;
//
//@Configuration
//public class DerivedTradeStreamsConfig {
//
//    @Bean
//    public KStream<String, DerivedTradeEvent> derivedTradeStream(StreamsBuilder builder) {
//
//        JsonSerde<TradeEvent> tradeEventSerde = new JsonSerde<>(TradeEvent.class);
//        JsonSerde<CaEvent> caEventSerde = new JsonSerde<>(CaEvent.class);
//        JsonSerde<DerivedTradeEvent> derivedTradeSerde = new JsonSerde<>(DerivedTradeEvent.class);
//
//        KStream<String, TradeEvent> tradeStream = builder.stream("trade-event",
//                Consumed.with(Serdes.String(), tradeEventSerde));
//
//        KStream<String, CaEvent> caStream = builder.stream("ca-event",
//                Consumed.with(Serdes.String(), caEventSerde));
//
//        // join 示例
//        KStream<String, DerivedTradeEvent> derivedStream = tradeStream.join(
//                caStream,
//                (trade, ca) -> {
//                    DerivedTradeEvent d = new DerivedTradeEvent();
//                    d.setSourceTradeId(trade.getTradeId());
//                    d.setCaEventId(ca.getCaEventId());
//                    d.setAccountId(trade.getAccountId());
//                    d.setInstrumentId(trade.getInstrumentId());
//                    d.setCaType(ca.getCaType());
//                    d.setEffectiveDate(ca.getEffectiveDate());
//                    d.setOriginalQty(trade.getQuantity());
//                    d.setAdjustedQty(trade.getQuantity().add(ca.getParam1())); // 举例
//                    return d;
//                },
//                JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofMinutes(5)),
//                StreamJoined.with(Serdes.String(), tradeEventSerde, caEventSerde)
//        );
//
//        derivedStream.to("derived-trade-event", Produced.with(Serdes.String(), derivedTradeSerde));
//
//        return derivedStream;
//    }
//}
