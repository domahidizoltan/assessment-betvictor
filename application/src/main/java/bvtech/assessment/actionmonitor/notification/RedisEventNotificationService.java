package bvtech.assessment.actionmonitor.notification;

import bvtech.assessment.actionmonitor.messaging.NotificationSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

@Slf4j
public class RedisEventNotificationService implements EventNotificationService {

    private final NotificationSender<String> notificationSender;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    public RedisEventNotificationService(final NotificationSender<String> notificationSender, final ObjectMapper objectMapper) {
        this.notificationSender = notificationSender;
        this.objectMapper = objectMapper;
        this.clock = Clock.systemUTC();
    }

    RedisEventNotificationService(final NotificationSender<String> notificationSender, final ObjectMapper objectMapper, final Clock clock) {
        this.notificationSender = notificationSender;
        this.objectMapper = objectMapper;
        this.clock = clock;
    }


    @Override
    public void eventNotification(String operation, String key) {
        parseOperation(operation).ifPresent(event -> {
            makeMessage(event, key).ifPresent(notificationSender::send);
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

    private Optional<Message<String>> makeMessage(RedisEvent event, String key) {
        EventNotificationDto dto = EventNotificationDto.of(Instant.now(clock), key, event.getEventType());
        Optional<Message<String>> message = Optional.empty();

        try {
            String json = objectMapper.writeValueAsString(dto);
            message = Optional.of(new GenericMessage<>(json));
        } catch (JsonProcessingException e) {
            log.error("Could not convert DTO to json: " + dto.toString(), e);
        }

        return message;
    }

}
