package bvtech.assessment.actionmonitor.notification;

import java.util.ArrayList;
import java.util.List;

import static bvtech.assessment.actionmonitor.notification.EventType.CREATE;
import static bvtech.assessment.actionmonitor.notification.EventType.DELETE;
import static bvtech.assessment.actionmonitor.notification.EventType.UPDATE;

public enum RedisEvent {

    RENAME_FROM (UPDATE),
    RENAME_TO (UPDATE),
    LPUSH (CREATE, UPDATE),
    RPUSH (CREATE, UPDATE),
    SADD (CREATE, UPDATE),
    SET (CREATE, UPDATE),
    LSET (UPDATE),
    RSET (UPDATE),
    LREM (UPDATE),
    RREM (UPDATE),
    SREM (DELETE),
    DEL (DELETE);

    private final List<EventType> eventTypes = new ArrayList<>();

    RedisEvent(EventType eventType, EventType... eventTypes) {
        this.eventTypes.add(eventType);
        for (int i = 0; i < eventTypes.length; i++) {
            this.eventTypes.add(eventType);
        }
    }

    EventType getEventType() {
        return eventTypes.get(0);
    }
}