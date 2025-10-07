package com.example.application.outboundservices.acl;

import csci318.demo.cargotracker.bookingms.domain.model.valueobjects.JobItinerary;
import csci318.demo.cargotracker.bookingms.domain.model.valueobjects.JobSpecification;
import csci318.demo.cargotracker.bookingms.domain.model.valueobjects.ProductionStep;
import csci318.demo.cargotracker.shareddomain.TransitEdge;
import csci318.demo.cargotracker.shareddomain.TransitPath;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Anti Corruption Service Class
 */

@Service
public class ExternalJobSchedulingService {

    /**
     * The Booking Bounded Context makes an external call to the Routing Service of the Routing Bounded Context to
     * fetch the Optimal Itinerary for a Cargo based on the Route Specification
     * @param jobSpecification
     * @return
     */
    public JobItinerary fetchJobForSpecification(JobSpecification jobSpecification){

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8785/jobscheduling/optimalRoute";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("submitDate",
                        jobSpecification.getSubmitDate().toString())
                .queryParam("startDate",
                        jobSpecification.getStartDate().toString())
                         .queryParam("projectedEndDate",
                jobSpecification.getProjectedEndDate().toString());
        // The getForObject method is used to make a GET request to the routing service
        TransitPath transitPath = restTemplate.getForObject(builder.toUriString(), TransitPath.class);
        assert transitPath != null;
        List<ProductionStep> productionSteps = new ArrayList<>(transitPath.getTransitEdges().size());
        for (TransitEdge edge : transitPath.getTransitEdges()) {
            productionSteps.add(toProductionStep(edge));
        }

        return new JobItinerary(productionSteps);

    }

    /**
     * Anti-corruption layer conversion method from the routing service's domain model (TransitEdges)
     * to the domain model recognized by the Booking Bounded Context (Legs)
     * @param edge
     * @return
     */
    private ProductionStep toProductionStep(TransitEdge edge) {
        return new ProductionStep(
                edge.getVoyageNumber(),
                edge.getFromUnLocode(),
                edge.getToUnLocode(),
                edge.getFromDate(),
                edge.getToDate());
        }
}
