package com.micro.pong.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ThrottleService {

    private static final int LIMIT = 1;

    private final AtomicInteger counter = new AtomicInteger();
    private volatile long windowStart = System.currentTimeMillis();

    public synchronized boolean tryProcess() {
        long now = System.currentTimeMillis();
        if (now - windowStart >= 1000) {
            windowStart = now;
            counter.set(0);
        }
        return counter.incrementAndGet() <= LIMIT;
    }

    public synchronized int getCurrentCount() {
        return counter.get();
    }
}
