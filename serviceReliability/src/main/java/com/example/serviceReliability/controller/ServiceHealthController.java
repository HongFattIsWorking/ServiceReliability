package com.example.serviceReliability.controller;

import com.example.serviceReliability.dto.HealthResponse;
import com.example.serviceReliability.service.HealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ServiceHealthController {
    @Autowired
    private HealthService healthService ;
    @Autowired

    @GetMapping("service_status")
    public ResponseEntity<List<HealthResponse>> getAllService(){
        return ResponseEntity.ok(healthService.getAllServiceCurrentStatus());
    }
}
