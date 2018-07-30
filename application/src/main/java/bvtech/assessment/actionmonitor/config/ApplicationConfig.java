package bvtech.assessment.actionmonitor.config;

import bvtech.assessment.actionmonitor.messaging.NotificationSender;
import bvtech.assessment.actionmonitor.notification.EventNotificationService;
import bvtech.assessment.actionmonitor.notification.RedisEventNotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    EventNotificationService redisEventNotificationService(final NotificationSender<String> webSocketNotificationSender,
                                                           final ObjectMapper objectMapper) {
        return new RedisEventNotificationService(webSocketNotificationSender, objectMapper);
    }
}
