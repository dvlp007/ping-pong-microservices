package com.micro.ping.controller;

import com.micro.ping.service.KafkaProducerService;
import com.micro.ping.service.PingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class PingController {

    private static final Logger log = LoggerFactory.getLogger(PingService.class);

    private final KafkaProducerService kafkaProducerService;

    public PingController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @Scheduled(fixedRate = 1000)
    public void sendHello() {
        String message = "Hello " + System.currentTimeMillis();
        kafkaProducerService.send(message);
        log.info("SUCCESS | Sent Kafka Message: {}", message);
    }
}
