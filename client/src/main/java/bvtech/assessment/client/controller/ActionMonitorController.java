package bvtech.assessment.client.controller;

import bvtech.assessment.client.config.NotificationServerProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ActionMonitorController {

    private NotificationServerProperties notificationServerProperties;

    private ActionMonitorController(final NotificationServerProperties notificationServerProperties) {
        this.notificationServerProperties = notificationServerProperties;
    }

    @RequestMapping("/action-monitor")
    public String actionMonitor(Model model) {
        final String wsEndpoint = notificationServerProperties.getUrl() + notificationServerProperties.getWebSocket().getEndpoint();
        final String wsTopic = notificationServerProperties.getWebSocket().getTopic();
        long pollInterval = notificationServerProperties.getActuator().getPollInterval();

        model.addAttribute("wsEndpoint", wsEndpoint);
        model.addAttribute("wsTopic", wsTopic);
        model.addAttribute("statusPollInterval", pollInterval);

        return "actionmonitor";
    }

}
