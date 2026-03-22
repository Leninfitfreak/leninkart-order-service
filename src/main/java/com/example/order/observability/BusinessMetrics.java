package com.example.order.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class BusinessMetrics {
    private final Counter orderFetchRequests;
    private final Counter orderEventsReceived;
    private final Counter ordersCreated;
    private final Counter orderProcessingFailures;
    private final Timer orderEventProcessingDuration;

    public BusinessMetrics(MeterRegistry registry) {
        this.orderFetchRequests = registry.counter("leninkart.orders.fetch.requests");
        this.orderEventsReceived = registry.counter("leninkart.orders.events.received");
        this.ordersCreated = registry.counter("leninkart.orders.created");
        this.orderProcessingFailures = registry.counter("leninkart.orders.processing.failure");
        this.orderEventProcessingDuration = registry.timer("leninkart.orders.event_processing.duration");
    }

    public void recordOrderFetchRequest() {
        orderFetchRequests.increment();
    }

    public void recordOrderEventReceived() {
        orderEventsReceived.increment();
    }

    public void recordOrderCreated() {
        ordersCreated.increment();
    }

    public void recordOrderProcessingFailure() {
        orderProcessingFailures.increment();
    }

    public Timer.Sample startOrderEventProcessing() {
        return Timer.start();
    }

    public void stopOrderEventProcessing(Timer.Sample sample) {
        sample.stop(orderEventProcessingDuration);
    }
}
