package bvtech.assessment.actionmonitor.integration;

import bvtech.assessment.actionmonitor.notification.EventType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class EventNotificationDto {

    private Instant instant;
    private String key;
    private EventType eventType;

    public EventNotificationDto() {}

    @JsonCreator
    public EventNotificationDto(@JsonProperty("instant") String instant,
                                @JsonProperty("key") String key,
                                @JsonProperty("eventType") String eventType) {

        this.instant = Instant.parse(instant);
        this.key = key;
        this.eventType = EventType.valueOf(eventType);
    }

    public Instant getInstant() {
        return instant;
    }

    public String getKey() {
        return key;
    }

    public EventType getEventType() {
        return eventType;
    }
}