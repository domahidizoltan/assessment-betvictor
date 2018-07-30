package bvtech.assessment.actionmonitor.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.core.MessageSendingOperations;

@Slf4j
public class WebSocketNotificationSender<T> implements NotificationSender<T> {

    private final MessageSendingOperations<String> messageSendingOperations;
    private final String topic;

    public WebSocketNotificationSender(final MessageSendingOperations messageSendingOperations, final String topic) {
        this.messageSendingOperations = messageSendingOperations;
        this.topic = topic;
    }

    @Override
    public void send(Message<T> message) {
        log.debug("Sending message: " + message.getPayload().toString());
        messageSendingOperations.convertAndSend(topic, message);
    }

}
