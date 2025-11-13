package com.example.serviceReliability.dto;

import lombok.Data;

@Data
public class MonitorConfig {
    private String name;
    private String url;
    private String expectedVersion;
    private String environment;
}
