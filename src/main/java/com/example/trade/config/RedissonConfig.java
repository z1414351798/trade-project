package com.example.trade.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean(destroyMethod="shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        config.setCodec(new JsonJacksonCodec(mapper));
        ClusterServersConfig clusterConfig = config.useClusterServers()
            .addNodeAddress(
                "redis://127.0.0.1:7001",
                "redis://127.0.0.1:7002",
                "redis://127.0.0.1:7003",
                "redis://127.0.0.1:7004",
                "redis://127.0.0.1:7005",
                "redis://127.0.0.1:7006"
            )
            .setScanInterval(2000)
            .setMasterConnectionPoolSize(64)
            .setSlaveConnectionPoolSize(64)
            .setReadMode(ReadMode.SLAVE)
            .setConnectTimeout(3000)
            .setTimeout(3000);
        return Redisson.create(config);
    }
}
