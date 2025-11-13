package com.example.serviceReliability.service;

import com.example.serviceReliability.dto.HealthResponse;

import java.util.List;

public interface HealthService {
    List<HealthResponse> getAllServiceCurrentStatus();
}
