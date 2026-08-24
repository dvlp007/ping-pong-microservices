package com.micro.pong.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PongService {

    private static final Logger log = LoggerFactory.getLogger(PongService.class);

    private final PersistenceService persistenceService;

    public PongService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public void processRequest(String message) {
        log.info("[Pong] Kafka received: {}", message);
        String response = "World";
        String status = "SUCCESS";
        persistenceService.saveToPostgres(message, response, status);
        persistenceService.saveToMongo(message, response, status);
    }
}
