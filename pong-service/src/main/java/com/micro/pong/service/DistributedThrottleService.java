package com.micro.pong.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "pong.throttle.distributed", havingValue = "true")
public class DistributedThrottleService {

    private final HazelcastInstance hazelcast;

    public DistributedThrottleService(HazelcastInstance hazelcast) {
        this.hazelcast = hazelcast;
    }

    public boolean tryProcess() {
        IMap<String, Integer> map = hazelcast.getMap("throttle-state");
        String key = String.valueOf(System.currentTimeMillis() / 1000);
        Integer count = map.compute(key, (k, v) -> v == null ? 1 : v + 1);
        return count <= 1;
    }
}
