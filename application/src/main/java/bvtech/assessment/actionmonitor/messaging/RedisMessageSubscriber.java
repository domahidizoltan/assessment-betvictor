package bvtech.assessment.actionmonitor.messaging;

import bvtech.assessment.actionmonitor.notification.EventNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.util.Optional;

@Slf4j
public class RedisMessageSubscriber implements MessageListener {

    private static final String DELIMITER = ":";

    private EventNotificationService eventNotificationServiceMock;

    public RedisMessageSubscriber(final EventNotificationService eventNotificationServiceMock) {
        this.eventNotificationServiceMock = eventNotificationServiceMock;
    }

    @Override
    public void onMessage(final Message message, final byte[] pattern) {
        final String channel = new String(message.getChannel());
        final String body = new String(message.getBody());
        log.debug("Received event [channel: {}, body: {}]", channel, body);

        Optional<String> operation = parseOperation(channel);
        operation.ifPresent(op -> eventNotificationServiceMock.eventNotification(op, body));
    }

    private Optional<String> parseOperation(final String channel) {
        Optional<String> operation = Optional.empty();

        if (channel.indexOf(DELIMITER) > -1) {
            operation = Optional.of(channel.split(DELIMITER)[1]);
        } else {
            log.error("Could not parse operation from channel " + channel);
        }

        return operation;
    }
}
