package bvtech.assessment.actionmonitor.messaging;

import bvtech.assessment.actionmonitor.notification.EventNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.data.redis.connection.DefaultMessage;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@ExtendWith(SpringExtension.class)
public class RedisMessageSubscriberTest {

    private static final String ANY_KEY = "anyKey";
    private static final String ANY_OPERATION = "anyOperation";
    private static final String PATTERN = "__keyevent@*";

    @Mock
    private EventNotificationService eventNotificationServiceMock;

    private MessageListener underTest;

    @BeforeEach
    public void setUp() {
        underTest = new RedisMessageSubscriber(eventNotificationServiceMock);
    }

    @Test
    public void shouldSendEventToProcess() {
        String channel = "__keyevent@0__:" + ANY_OPERATION;
        Message message = new DefaultMessage(channel.getBytes(), ANY_KEY.getBytes());

        underTest.onMessage(message, PATTERN.getBytes());

        verify(eventNotificationServiceMock).eventNotification(ANY_OPERATION, ANY_KEY);
    }

    @Test
    public void shouldNotSendEventWhenNotificationCouldNotBeProcessed() {
        Message message = new DefaultMessage("invalid-channel".getBytes(), ANY_KEY.getBytes());

        underTest.onMessage(message, PATTERN.getBytes());

        verifyZeroInteractions(eventNotificationServiceMock);
    }
}
