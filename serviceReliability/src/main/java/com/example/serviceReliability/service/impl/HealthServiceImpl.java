package com.example.serviceReliability.service.impl;

import com.example.serviceReliability.dto.HealthResponse;
import com.example.serviceReliability.entity.ServiceHealth;
import com.example.serviceReliability.repository.ServiceHealthRepository;
import com.example.serviceReliability.service.HealthCheckService;
import com.example.serviceReliability.service.HealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HealthServiceImpl implements HealthService {

    @Autowired
    private HealthCheckService healthCheckService;
    @Autowired
    private ServiceHealthRepository serviceHealthRepository;

    @Override
    public List<HealthResponse> getAllServiceCurrentStatus() {
        healthCheckService.checkAllOnce();
        List<ServiceHealth> serviceHealthList = serviceHealthRepository.getLatestHealth();
        return mapResponse(serviceHealthList);
    }

    private List<HealthResponse> mapResponse(List<ServiceHealth> serviceHealthList){
        List<HealthResponse> healthResponses = new ArrayList<>();
        for(ServiceHealth serviceHealth : serviceHealthList){
            HealthResponse healthResponse = new HealthResponse();
            healthResponse.setEnvironment(serviceHealth.getEnvironment());
            healthResponse.setLatency(serviceHealth.getLatencyMs());
            healthResponse.setServiceName(serviceHealth.getName());
            healthResponse.setHttpStatus(serviceHealth.getStatus());
            healthResponse.setTimeStamp(serviceHealth.getTimestamp().toString());
            healthResponse.setVersionFound(serviceHealth.getVersionFound());
            healthResponses.add(healthResponse);
        }
        return healthResponses;
    }
}
