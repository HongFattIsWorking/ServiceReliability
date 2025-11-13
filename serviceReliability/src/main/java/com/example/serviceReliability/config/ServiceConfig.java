package com.example.serviceReliability.config;


import com.example.serviceReliability.dto.MonitorConfig;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "")
public class ServiceConfig {


    private List<MonitorConfig> services;

    private Poll poll = new Poll();

    @Data
    public static class Poll {
        private long intervalMs = 15000L;

        public void setIntervalMs(long intervalMs) { this.intervalMs = intervalMs; }
    }


}
