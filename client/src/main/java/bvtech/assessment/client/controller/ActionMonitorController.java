package bvtech.assessment.client.controller;

import bvtech.assessment.client.config.MessagingProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ActionMonitorController {

    private MessagingProperties messagingProperties;

    private ActionMonitorController(final MessagingProperties messagingProperties) {
        this.messagingProperties = messagingProperties;
    }

    @RequestMapping("/action-monitor")
    public String actionMonitor(Model model) {
        model.addAttribute("wsEndpoint", messagingProperties.getUrl() + messagingProperties.getWebsocketEndpoint());
        model.addAttribute("wsTopic", messagingProperties.getTopic());
        return "actionmonitor";
    }

}
