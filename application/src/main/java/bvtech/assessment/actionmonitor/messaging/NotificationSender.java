package bvtech.assessment.actionmonitor.messaging;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;

public interface NotificationSender<T> {

    void send(Message<T> message) throws MessagingException;

}
