package com.micro.ping.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PingService {

    private static final Logger log = LoggerFactory.getLogger(PingService.class);

    private final WebClient webClient;

    public PingService(@Value("${pong.url}") String pongUrl) {
        this.webClient = WebClient.builder().baseUrl(pongUrl).build();
    }

    @Scheduled(fixedRate = 1000)
    public void sendHello() {
        webClient.post()
                .uri("/pong")
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue("Hello")
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                        response -> log.info("SUCCESS | Sent Hello, Pong responded: {}", response),
                        error -> log.error("ERROR | {}", error.getMessage())
                );
    }
}
