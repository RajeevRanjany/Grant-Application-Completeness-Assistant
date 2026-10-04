package com.grant.assistant.controller;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    private final EntityManager entityManager;

    @Autowired
    public HealthController(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        String dbStatus;
        try {
            entityManager.createNativeQuery("SELECT 1").getSingleResult();
            dbStatus = "ok";
        } catch (Exception e) {
            dbStatus = "unavailable";
        }
        return Map.of("status", "ok", "db", dbStatus);
    }
}
