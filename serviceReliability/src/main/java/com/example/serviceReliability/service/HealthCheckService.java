package com.example.serviceReliability.service;

import com.example.serviceReliability.dto.MonitorConfig;
import com.example.serviceReliability.entity.ServiceHealth;

public interface HealthCheckService {
    void checkAllOnce();
    ServiceHealth checkOne(MonitorConfig monitoringConfig);

}
