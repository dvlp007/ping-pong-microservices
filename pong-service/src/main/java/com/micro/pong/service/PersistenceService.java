package com.micro.pong.service;

import com.micro.pong.model.RequestRecord;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PersistenceService {

    private final JdbcTemplate jdbcTemplate;
    private final MongoTemplate mongoTemplate;

    public PersistenceService(JdbcTemplate jdbcTemplate, MongoTemplate mongoTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.mongoTemplate = mongoTemplate;
    }

    public void saveToPostgres(String request, String response, String status) {
        jdbcTemplate.update(
                "INSERT INTO request_log (request, response, status) VALUES (?, ?, ?)",
                request, response, status
        );
    }

    public void saveToMongo(String request, String response, String status) {
        RequestRecord record = new RequestRecord(request, response, status, Instant.now());
        mongoTemplate.save(record);
    }
}
