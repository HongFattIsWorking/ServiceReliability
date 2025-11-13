package com.example.serviceReliability.service.impl;

import com.example.serviceReliability.config.ServiceConfig;
import com.example.serviceReliability.dto.MonitorConfig;
import com.example.serviceReliability.entity.ServiceHealth;
import com.example.serviceReliability.repository.ServiceHealthRepository;
import com.example.serviceReliability.service.HealthCheckService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@EnableScheduling
public class HealthCheckServiceImpl implements HealthCheckService {
    private final ServiceConfig config;

    @Autowired
    private final ServiceHealthRepository serviceHealthRepository;
    private final HttpClient client;
    private final ObjectMapper mapper = new ObjectMapper();


    public HealthCheckServiceImpl(ServiceConfig config, ServiceHealthRepository serviceHealthRepository) {
        this.config = config;
        this.serviceHealthRepository = serviceHealthRepository;
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }


    @PostConstruct
    public void initialRun() {
        checkAllOnce();
    }


    private volatile long lastRun = 0;

    @Scheduled(fixedDelay = 5000)
    public void scheduledRun() {
        long now = System.currentTimeMillis();
        if (now - lastRun >= config.getPoll().getIntervalMs()) {
            lastRun = now;
            checkAllOnce();
        }
    }

    @Override
    public void checkAllOnce() {
        List<MonitorConfig> services = config.getServices();
        if (services == null || services.isEmpty()) return;
        for (MonitorConfig s : services) {
            try {
                ServiceHealth health = checkOne(s);
                serviceHealthRepository.save(health);

            } catch (Exception e) {
                ServiceHealth downService = new ServiceHealth();
                downService.setName(s.getName());
                downService.setUrl(s.getUrl());
                downService.setTimestamp(LocalDateTime.now());
                downService.setHttpStatus(-1);
                downService.setLatencyMs(-1);
                downService.setVersionFound(null);
                downService.setStatus("DOWN");
                downService.setEnvironment(s.getEnvironment());
                serviceHealthRepository.save(downService);
            }
        }
    }

    @Override
    public ServiceHealth checkOne(MonitorConfig monitoringConfig) {
        ServiceHealth serviceHealth = new ServiceHealth();
        serviceHealth.setName(monitoringConfig.getName());
        serviceHealth.setTimestamp(LocalDateTime.now());
        long start = System.nanoTime();

        HttpRequest.Builder builder = HttpRequest.newBuilder();
        builder.uri(URI.create(monitoringConfig.getUrl()));
        builder.timeout(Duration.ofSeconds(10));
        builder.GET();
        HttpRequest req = builder
                .build();

        try {
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            long latencyMs = (System.nanoTime() - start) / 1_000_000;
            serviceHealth.setLatencyMs(latencyMs);
            serviceHealth.setUrl(monitoringConfig.getUrl());
            serviceHealth.setName(monitoringConfig.getName());
            serviceHealth.setHttpStatus(resp.statusCode());
            serviceHealth.setEnvironment(monitoringConfig.getEnvironment());

            String version = extractVersion(resp.body(), resp,monitoringConfig);
            serviceHealth.setVersionFound(version);

            // Decide status
            String status;
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                if (monitoringConfig.getExpectedVersion() != null && monitoringConfig.getExpectedVersion().length() > 0) {
                    if (version == null) {
                        status = "UNKNOWN";
                    } else if (version.equals(monitoringConfig.getExpectedVersion())) {
                        status = "UP";
                    } else {
                        status = "DEGRADED";
                    }
                } else {
                    status = "UP";
                }
            } else {
                status = "DOWN";
            }
            serviceHealth.setStatus(status);
            return serviceHealth;
        } catch (Exception e) {
            long latencyMs = (System.nanoTime() - start) / 1_000_000;
            serviceHealth.setLatencyMs(latencyMs);
            serviceHealth.setHttpStatus(-1);
            serviceHealth.setVersionFound(null);
            serviceHealth.setStatus("DOWN");
            return serviceHealth;
        }
    }

    private String extractVersion(String body, HttpResponse<String> resp,MonitorConfig config) {
        if (body != null && body.trim().startsWith("{")) {
            try {
                JsonNode root = mapper.readTree(body);
                if (root.has("version")) return root.get("version").asText();
                if (root.has("build") && root.get("build").has("version")) return root.get("build").get("version").asText();
                if (root.has("data") && root.get("data").has("version")) return root.get("data").get("version").asText();
            } catch (Exception ignored) {}
        }
        Optional<String> xVersion = resp.headers().firstValue("X-Version");
        return xVersion.orElseGet(() -> resp.headers().firstValue("Server").orElse(null));
    }


}
