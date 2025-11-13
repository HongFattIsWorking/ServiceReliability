package com.example.serviceReliability.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ServiceHealth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String url;
    private LocalDateTime timestamp;
    private int httpStatus;
    private long latencyMs;
    private String environment;
    private String versionFound;
    private String status;

}
