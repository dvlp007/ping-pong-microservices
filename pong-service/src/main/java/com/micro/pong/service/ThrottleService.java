package com.micro.pong.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ThrottleService {

    /**
     * 窗口大小，单位毫秒
     */
    public static final int WINDOW_SIZE = 1000;
    /**
     * 限制每秒最多处理1个请求
     */
    private static final int LIMIT = 1;

    /**
     * 当前窗口内处理的请求数量
      */
    private final AtomicInteger counter = new AtomicInteger();
    /**
     * 当前窗口开始时间
     */
    private volatile long windowStart = System.currentTimeMillis();

    /**
     * 固定窗口限流
     * 每秒最多处理1个请求
     */
    public synchronized boolean tryProcess() {
        long now = System.currentTimeMillis();
        if (now - windowStart >= WINDOW_SIZE) {
            windowStart = now;
            counter.set(0);
        }
        return counter.incrementAndGet() <= LIMIT;
    }

}
