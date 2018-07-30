package bvtech.assessment.actionmonitor.messaging;

import org.springframework.messaging.Message;

public interface NotificationSender<T> {

    void send(Message<T> message);

}
