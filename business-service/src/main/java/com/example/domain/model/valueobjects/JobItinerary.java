package com.example.domain.model.valueobjects;

import jakarta.persistence.*;

import java.util.Collections;
import java.util.List;

@Embeddable
public class JobItinerary {

    public static final JobItinerary EMPTY_ITINERARY = new JobItinerary();
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "job_id")
    @OrderBy("scheduleOrder")
    private List<ProductionStep> productionSteps = Collections.emptyList();

    public JobItinerary() {
        // Nothing to initialize.
    }

    public JobItinerary(List<ProductionStep> productionSteps) {
        this.productionSteps = productionSteps;
    }

    public List<ProductionStep> getLegs() {
        return Collections.unmodifiableList(productionSteps);
    }
}
