package com.example.controller;

import com.example.application.MaterialUsageQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST endpoint for querying recent aggregated material usage.
 */
@RestController
public class AnalyticsController {

    private final MaterialUsageQueryService materialUsageQueryService;

    public AnalyticsController(MaterialUsageQueryService materialUsageQueryService) {
        this.materialUsageQueryService = materialUsageQueryService;
    }

    @GetMapping("/api/analytics/material-usage")
    public List<Map.Entry<String, Long>> getRecentUsage() {
        return materialUsageQueryService.getRecentMaterialUsage();
    }
}
