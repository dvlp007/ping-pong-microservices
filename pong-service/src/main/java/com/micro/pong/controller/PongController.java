package com.micro.pong.controller;

import com.micro.pong.service.ThrottleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class PongController {

    private static final Logger log = LoggerFactory.getLogger(PongController.class);

    private final ThrottleService throttleService;

    public PongController(ThrottleService throttleService) {
        this.throttleService = throttleService;
    }

    @PostMapping(value = "/pong", produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<ResponseEntity<String>> handlePing(@RequestBody String message) {
        if (!throttleService.tryProcess()) {
            return Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Throttled"));
        }
        log.info("Received: {}", message);
        return Mono.just(ResponseEntity.ok("World"));
    }
}
