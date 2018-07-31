package bvtech.assessment.actionmonitor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("messaging")
@Data
public class MessagingProperties {

    private String websocketEndpoint;
    private Topic topic;

    @Data
    static class Topic {
        private String eventPattern;
        private String destination;
    }
}
