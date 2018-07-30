package bvtech.assessment.actionmonitor.messaging;

import bvtech.assessment.actionmonitor.notification.EventNotificationDto;
import bvtech.assessment.actionmonitor.notification.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
public class WebSocketNotificationSenderTest {

    private static final String MESSAGING_TOPIC = "/anymessaging/topic";
    private static final EventNotificationDto ANY_EVENT_DTO = EventNotificationDto.of(Instant.now(), "anyKey", EventType.DELETE);
    private static Message<EventNotificationDto> ANY_MESSAGE = new GenericMessage<>(ANY_EVENT_DTO);

    @Mock
    private MessageSendingOperations<String> messageSendingOperationsMock;

    private NotificationSender<EventNotificationDto> underTest;

    @BeforeEach
    public void setUp() {
        underTest = new WebSocketNotificationSender<>(messageSendingOperationsMock, MESSAGING_TOPIC);
    }

    @Test
    public void shouldSendNotification() {
        underTest.send(ANY_MESSAGE);

        verify(messageSendingOperationsMock).convertAndSend(MESSAGING_TOPIC, ANY_MESSAGE);
    }

    @Test
    public void shouldThrowExceptionWhenCouldNotSendMessage() {
        willThrow(MessagingException.class)
            .given(messageSendingOperationsMock).convertAndSend(MESSAGING_TOPIC, ANY_MESSAGE);

        assertThrows(MessagingException.class, () -> underTest.send(ANY_MESSAGE));
    }

}
