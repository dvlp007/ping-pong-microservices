package com.micro.pong.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private final PongService pongService;

    public KafkaConsumerService(PongService pongService) {
        this.pongService = pongService;
    }

    @KafkaListener(topics = "ping-pong-topic", groupId = "pong-group")
    public void consume(String message) {
        pongService.processRequest(message);
    }
}
