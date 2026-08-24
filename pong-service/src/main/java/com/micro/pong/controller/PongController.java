package com.micro.pong.controller;

import com.micro.pong.service.DistributedThrottleService;
import com.micro.pong.service.ThrottleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
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
    private final ObjectProvider<DistributedThrottleService> distributedThrottleService;
    private final boolean distributed;

    public PongController(ThrottleService throttleService,
                          ObjectProvider<DistributedThrottleService> distributedThrottleService,
                          @Value("${pong.throttle.distributed:false}") boolean distributed) {
        this.throttleService = throttleService;
        this.distributedThrottleService = distributedThrottleService;
        this.distributed = distributed;
    }

    @PostMapping(value = "/pong", produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<ResponseEntity<String>> handlePing(@RequestBody String message) {
        boolean allowed = distributed
                ? distributedThrottleService.getObject().tryProcess()
                : throttleService.tryProcess();
        if (!allowed) {
            return Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Throttled"));
        }
        log.info("Received: {}", message);
        return Mono.just(ResponseEntity.ok("World"));
    }
}
