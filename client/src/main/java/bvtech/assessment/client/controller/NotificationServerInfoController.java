package bvtech.assessment.client.controller;

import bvtech.assessment.client.config.NotificationServerProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class NotificationServerInfoController {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationServerInfoController.class);

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    private String actuatorEndpoint;

    public NotificationServerInfoController(final RestTemplate restTemplate,
                                            final ObjectMapper objectMapper,
                                            final NotificationServerProperties notificationServerProperties) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        actuatorEndpoint = notificationServerProperties.getUrl() + notificationServerProperties.getActuator().getEndpoint();
    }

    @RequestMapping("/notification-server-info")
    public String serverInfo(Model model) {
        model.addAttribute("version", getVersionInfo());
        model.addAttribute("status", getHealthStatus());
        return "serverinfo";
    }

    private String getVersionInfo() {
        String version = "";
        try {
            String info = restTemplate.getForObject(actuatorEndpoint + "/info", String.class);

            JsonNode jsonNode = objectMapper.readTree(info);
            version = parseVersionInfo(jsonNode);
        } catch (IOException|RestClientException e) {
            LOG.error("Could not read notification server version: " + e.getMessage());
        }

        return version;
    }

    private String getHealthStatus() {
        String status = "NOT OK";
        try {
            String info = restTemplate.getForObject(actuatorEndpoint + "/health", String.class);

            JsonNode jsonNode = objectMapper.readTree(info);
            List<String> statusString = jsonNode.findValuesAsText("status");
            status = hasStatusUp(statusString) ? "OK" : "NOT OK";
        } catch (IOException|RestClientException e) {
            LOG.error("Could not read notification server health status: " + e.getMessage());
        }

        return status;
    }

    private boolean hasStatusUp(List<String> statusString) {
        return !statusString.isEmpty() && "UP".equals(statusString.get(0));
    }

    private String parseVersionInfo(JsonNode jsonNode) {
        String version;
        version = Optional.ofNullable(jsonNode.findValue("app"))
            .map(node -> node.findValuesAsText("version"))
            .filter(list -> !list.isEmpty())
            .map(list -> list.get(0))
            .orElse("");
        return version;
    }

}
