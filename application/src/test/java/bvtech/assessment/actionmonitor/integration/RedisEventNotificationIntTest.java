package bvtech.assessment.actionmonitor.integration;

import bvtech.assessment.actionmonitor.notification.EventType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RedisEventNotificationIntTest {

    @Value("${local.server.port}")
    private int port;

    @Value("${messaging.topic.destination}")
    private String topic;

    @Value("${messaging.websocket-endpoint}")
    private String websocketEndpoint;

    private static final String TEST_KEY = "betvictor#testKey";

    private final CountDownLatch countDownLatch = new CountDownLatch(2);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private List<EventNotificationDto> results = new ArrayList<>(2);

    @Test
    public void shouldSendNotificationsAboutObjectCreationAndDeletion() throws InterruptedException, ExecutionException, TimeoutException {
        results.clear();
        String url = "ws://localhost:" + port + websocketEndpoint;
        createWSClient(url, topic);

        redisTemplate.boundSetOps(TEST_KEY).add("anyValue");
        redisTemplate.delete(TEST_KEY);
        countDownLatch.await();

        assertThat(results.size(), is(2));
        results.sort(Comparator.comparing(EventNotificationDto::getInstant));
        assertThat(results.get(0).getEventType(), is(EventType.CREATE));
        assertThat(results.get(1).getEventType(), is(EventType.DELETE));
        results.forEach(r -> assertThat(r.getKey(), is(TEST_KEY)));
    }

    private WebSocketStompClient createWSClient(final String url, final String topic)
        throws InterruptedException, ExecutionException, TimeoutException {

        WebSocketTransport webSocketTransport = new WebSocketTransport(new StandardWebSocketClient());
        SockJsClient sockJsClient = new SockJsClient(Arrays.asList(webSocketTransport));

        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSessionHandler sessionHandler = new MySessionHandler();
        ListenableFuture<StompSession> stompSessionFuture = stompClient.connect(url, sessionHandler);

        StompSession stompSession = stompSessionFuture.get(5, TimeUnit.SECONDS);
        stompSession.subscribe(topic, new MySessionHandler());

        return stompClient;
    }

    class MySessionHandler implements StompSessionHandler {

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            System.out.println("Connected");
        }

        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
            exception.printStackTrace();
        }

        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
            exception.printStackTrace();
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return Object.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object message) {
            try {
                Map<String, Object> msg = (Map<String, Object>) message;
                EventNotificationDto eventNotificationDto = objectMapper.readValue((String) msg.get("payload"), EventNotificationDto.class);
                results.add(eventNotificationDto);
            } catch (IOException e) {
                e.printStackTrace();
            }
            countDownLatch.countDown();
        }
    }

}
