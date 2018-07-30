package bvtech.assessment.actionmonitor.notification;

import bvtech.assessment.actionmonitor.messaging.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

@Slf4j
public class RedisEventNotificationService implements EventNotificationService {

    private final NotificationSender<EventNotificationDto> notificationSender;
    private final Clock clock;

    public RedisEventNotificationService(final NotificationSender<EventNotificationDto> notificationSender) {
        this.notificationSender = notificationSender;
        this.clock = Clock.systemUTC();
    }

    RedisEventNotificationService(final NotificationSender<EventNotificationDto> notificationSender, final Clock clock) {
        this.notificationSender = notificationSender;
        this.clock = clock;
    }


    @Override
    public void eventNotification(String operation, String key) {
        parseOperation(operation).ifPresent(event -> {
            Message<EventNotificationDto> message = makeMessage(event, key);
            notificationSender.send(message);
        });
    }

    private Optional<RedisEvent> parseOperation(String operation) {
        Optional<RedisEvent> event = Optional.empty();
        try {
            RedisEvent redisEvent = RedisEvent.valueOf(operation.toUpperCase());
            event = Optional.of(redisEvent);
        } catch (IllegalArgumentException ex) {
            log.error("Operation {} not exists for Redis", operation, ex);
        }

        return event;
    }

    private Message<EventNotificationDto> makeMessage(RedisEvent event, String key) {
        EventNotificationDto dto = EventNotificationDto.of(Instant.now(clock), key, event.getEventType() );
        return new GenericMessage<>(dto);
    }

}
