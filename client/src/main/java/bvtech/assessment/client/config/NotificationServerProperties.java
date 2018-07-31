package bvtech.assessment.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("notification-server")
@Data
public class NotificationServerProperties {

    private String url;
    private WebSocket webSocket;
    private Actuator actuator;

    @Data
    public static class WebSocket {

        private String endpoint;
        private String topic;

    }

    @Data
    public static class Actuator {

        private String endpoint;
        private long pollInterval;

    }
}
