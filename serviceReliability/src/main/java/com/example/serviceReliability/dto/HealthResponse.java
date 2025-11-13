package com.example.serviceReliability.dto;

import lombok.Data;

@Data
public class HealthResponse {
    private String serviceName;
    private long latency;
    private String httpStatus;
    private String environment;
    private String timeStamp;
    private String versionFound;
}
