package com.micro.ping.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PingService {

    private static final Logger log = LoggerFactory.getLogger(PingService.class);

    private final WebClient webClient;
    private final RateLimitService rateLimitService;

    public PingService(@Value("${pong.url}") String pongUrl, RateLimitService rateLimitService) {
        this.webClient = WebClient.builder().baseUrl(pongUrl).build();
        this.rateLimitService = rateLimitService;
    }

    @Scheduled(fixedRate = 900)
    public void sendHello() {
        if (!rateLimitService.tryAcquire()) {
            log.info("RATE_LIMITED | Request not sent - global rate limit reached");
            return;
        }
        webClient.post()
                .uri("/pong")
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue("Hello")
                .retrieve()
                .onStatus(status -> status.value() == HttpStatus.TOO_MANY_REQUESTS.value(),
                        response -> {
                            log.info("PONG_THROTTLED | Request sent but Pong throttled it (429)");
                            return Mono.error(new PongThrottledException());
                        })
                .bodyToMono(String.class)
                .subscribe(
                        response -> log.info("SUCCESS | Request sent & Pong responded: {}", response),
                        error -> {
                            if (!(error instanceof PongThrottledException)) {
                                log.error("ERROR | {}", error.getMessage());
                            }
                        }
                );
    }

    private static final class PongThrottledException extends RuntimeException {
        private PongThrottledException() {
            super("Pong returned 429");
        }
    }
}
