package com.example.controller;

import com.example.application.MaterialUsageQueryService;
import com.example.interfaces.dto.MaterialConsumedByName;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST endpoint for querying recent aggregated material usage.
 */
@Controller    // This means that this class is a Controller
@RequestMapping("/queries")
public class AnalyticsController {

    private final MaterialUsageQueryService materialUsageQueryService;

    public AnalyticsController(MaterialUsageQueryService materialUsageQueryService) {
        this.materialUsageQueryService = materialUsageQueryService;
    }

    @GetMapping("/api/analytics/material-usage")
    public List<MaterialConsumedByName> getRecentUsage() {
        return materialUsageQueryService.getRecentMaterialUsage();
    }
}
