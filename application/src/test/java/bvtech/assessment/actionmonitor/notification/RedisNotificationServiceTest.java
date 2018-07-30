package bvtech.assessment.actionmonitor.notification;

import bvtech.assessment.actionmonitor.messaging.NotificationSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.messaging.Message;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Clock;
import java.time.Instant;

import static bvtech.assessment.actionmonitor.notification.EventType.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@ExtendWith(SpringExtension.class)
public class RedisNotificationServiceTest {

    private static final Instant ANY_INSTANT = Instant.now();
    private static final String ANY_KEY = "any_key";

    @Mock
    private NotificationSender<EventNotificationDto> notificationSenderMock;
    @Mock
    private Clock clockMock;

    private ArgumentCaptor<Message<EventNotificationDto>> messageCaptor = ArgumentCaptor.forClass(Message.class);

    private EventNotificationService underTest;

    @BeforeEach
    public void setUp() {
        given(clockMock.instant()).willReturn(ANY_INSTANT);
        underTest = new RedisEventNotificationService(notificationSenderMock, clockMock);
    }

    @ParameterizedTest
    @ValueSource(strings = {"lpush","sadd","set"})
    public void shouldSendCreateNotification(final String operation) {
        EventNotificationDto expectedDto = makeAnyDtoWith(CREATE);

        underTest.eventNotification(operation, ANY_KEY);

        verifyMessageWith(expectedDto);
    }

    @ParameterizedTest
    @ValueSource(strings = {"rename_from","rename_to","lset","lrem"})
    public void shouldSendUpdateNotification(final String operation) {
        EventNotificationDto expectedDto = makeAnyDtoWith(UPDATE);

        underTest.eventNotification(operation, ANY_KEY);

        verifyMessageWith(expectedDto);
    }

    @ParameterizedTest
    @ValueSource(strings = {"del"})
    public void shouldSendDeleteNotification(final String operation) {
        EventNotificationDto expectedDto = makeAnyDtoWith(DELETE);

        underTest.eventNotification(operation, ANY_KEY);

        verifyMessageWith(expectedDto);
    }

    @Test
    public void shouldNotSendMessageWhenOperationNotFound() {
        underTest.eventNotification("non-existent", ANY_KEY);

        verifyZeroInteractions(notificationSenderMock);
    }

    private EventNotificationDto makeAnyDtoWith(final EventType eventType) {
        return EventNotificationDto.of(ANY_INSTANT, ANY_KEY, eventType);
    }

    private void verifyMessageWith(EventNotificationDto expectedDto) {
        verify(notificationSenderMock).send(messageCaptor.capture());
        EventNotificationDto actualDto = messageCaptor.getValue().getPayload();
        assertThat(expectedDto, is(actualDto));
    }

}
