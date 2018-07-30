package bvtech.assessment.actionmonitor.config;

import bvtech.assessment.actionmonitor.messaging.NotificationSender;
import bvtech.assessment.actionmonitor.messaging.RedisMessageSubscriber;
import bvtech.assessment.actionmonitor.messaging.WebSocketNotificationSender;
import bvtech.assessment.actionmonitor.notification.EventNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.messaging.core.MessageSendingOperations;

@Configuration
@EnableConfigurationProperties(MessagingProperties.class)
public class MessagingConfig {

    @Autowired
    private MessagingProperties messagingProperties;

    @Bean
    MessageListener redisMessageSubscriber(final EventNotificationService redisEventNotificationService) {
        return new RedisMessageSubscriber(redisEventNotificationService);
    }

    @Bean
    MessageListenerAdapter listenerAdapter(final MessageListener redisMessageSubscriber) {
        return new MessageListenerAdapter(redisMessageSubscriber, "onMessage");
    }

    @Bean
    RedisMessageListenerContainer messageListener(final RedisConnectionFactory connectionFactory,
                                                  final MessageListenerAdapter listenerAdapter) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        final String eventTopicPattern = messagingProperties.getTopic().getEventPattern();
        PatternTopic patternTopic = new PatternTopic(eventTopicPattern);
        container.addMessageListener(listenerAdapter, patternTopic);

        return container;
    }

    @Bean
    NotificationSender<String> webSocketNotificationSender(final MessageSendingOperations<String> messageSendingOperations) {

        final String destinationTopic = messagingProperties.getTopic().getDestination();
        return new WebSocketNotificationSender<>(messageSendingOperations, destinationTopic);
    }
}
