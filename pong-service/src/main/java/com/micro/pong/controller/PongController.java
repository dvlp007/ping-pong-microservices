package com.micro.pong.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class PongController {

    private static final Logger log = LoggerFactory.getLogger(PongController.class);

    @PostMapping(value = "/pong", produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> pong(@RequestBody String message) {
        log.info("Received: {}", message);
        return Mono.just("World");
    }
}
