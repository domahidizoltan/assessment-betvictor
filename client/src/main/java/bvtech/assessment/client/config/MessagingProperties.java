package bvtech.assessment.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("messaging")
public class MessagingProperties {

    private String url;
    private String websocketEndpoint;
    private String topic;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWebsocketEndpoint() {
        return websocketEndpoint;
    }

    public void setWebsocketEndpoint(String websocketEndpoint) {
        this.websocketEndpoint = websocketEndpoint;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
