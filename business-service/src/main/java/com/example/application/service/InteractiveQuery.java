package com.example.application.service;

import com.example.interfaces.rest.dto.MaterialAmountByMachineId;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;


//IMPORTANT: code taken from CSCI318 lab example Cargo Tracker (Stream Processing) InteractiveQuery class
//variable names have been changed and a composite serdeKey has been used instead
//" start quote
/**
 * A Spring Service that provides an interface for querying the underlying Kafka Streams state stores.
 * This class uses the {@link InteractiveQueryService} to expose the aggregated stream data,
 * allowing other parts of the application (like REST controllers) to access the results of the stream processing.
 */
@Service
public class InteractiveQuery {

    private final InteractiveQueryService interactiveQueryService;
    @Value("${windowstore.name}")
    private String WINDOWSTORE_NAME;
    @Value("${window.size.ms}")
    private long WINDOW_SIZE_MS;

    public InteractiveQuery(InteractiveQueryService interactiveQueryService) {
        this.interactiveQueryService = interactiveQueryService;
    }

    /**
     * Fetches the total booking amounts for each city from the most recently completed time window.
     *
     * @return A list of {@link MaterialAmountByMachineId} objects.
     */
    public List<MaterialAmountByMachineId> getWindowedMaterialAmountByMachineId() {
        List<MaterialAmountByMachineId> windowedMachineMaterialAmounts = new ArrayList<>();
        long now = Instant.now().toEpochMilli();
        // Calculate the time range for the most recently *completed* window.
        // This logic targets the window that has just finished processing, ensuring we query a complete set of data.
        // (now / WINDOW_SIZE_MS) is an integer division, rounding down to the start of the *current* window.
        // We then subtract one window size to get the start time of the *previous* (i.e., most recently completed) window.
        long targetWindowStartTime = (now / WINDOW_SIZE_MS) * WINDOW_SIZE_MS - WINDOW_SIZE_MS;
        Instant timeFrom = Instant.ofEpochMilli(targetWindowStartTime);
        Instant timeTo = timeFrom.plus(Duration.ofMillis(WINDOW_SIZE_MS));

        // Query the state store for the calculated time window.
        try (KeyValueIterator<Windowed<MachineMaterialKey>, Long> all = getWindowedSchedulesKSStore().fetchAll(timeFrom, timeTo)) {
            while (all.hasNext()) {
                KeyValue<Windowed<MachineMaterialKey>, Long> ks = all.next();
                MaterialAmountByMachineId amountPerSchedule = new MaterialAmountByMachineId();
                // The city name is decorated with the window start and end times for clarity.
                amountPerSchedule.setMachineId(ks.key.key() + " (window: " + ks.key.window().startTime() + " - "
                        + ks.key.window().endTime() + ")");
                amountPerSchedule.setMaterialAmount(ks.value);
                windowedMachineMaterialAmounts.add(amountPerSchedule);
            }
        }
        return windowedMachineMaterialAmounts;
    }

    /**
     * Helper method to retrieve the read-only window store for booking analytics.
     * It uses the InteractiveQueryService to get a queryable handle to the state store.
     *
     * @return A {@link ReadOnlyWindowStore} that can be queried for aggregated booking data.
     */
    private ReadOnlyWindowStore<MachineMaterialKey, Long> getWindowedSchedulesKSStore() {
        return this.interactiveQueryService.getQueryableStore(WINDOWSTORE_NAME,
                QueryableStoreTypes.windowStore());
    }

}
//" end quote
