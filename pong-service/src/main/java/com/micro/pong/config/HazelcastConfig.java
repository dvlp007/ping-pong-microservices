package com.micro.pong.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "pong.throttle.distributed", havingValue = "true")
public class HazelcastConfig {

    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();
        config.setInstanceName("pong-hz-instance");
        config.addMapConfig(new MapConfig("throttle-state").setTimeToLiveSeconds(1));
        return Hazelcast.newHazelcastInstance(config);
    }
}
