package com.micro.pong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class PongController {

    @GetMapping("/pong")
    public Mono<Map<String, String>> pong() {
        return Mono.just(Map.of(
                "service", "pong-service",
                "message", "pong"
        ));
    }
}
