package com.micro.pong.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PongService {

    private static final Logger log = LoggerFactory.getLogger(PongService.class);

    public void processRequest(String message) {
        log.info("[Pong] Kafka received: {}", message);
    }
}
