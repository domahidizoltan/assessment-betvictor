package bvtech.assessment.actionmonitor.notification;

import lombok.Value;

import java.time.Instant;

@Value(staticConstructor = "of")
public class EventNotificationDto {

    private Instant instant;
    private String key;
    private EventType eventType;

}
