package com.fluffytime.domain.notification.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NotificationController {

    @GetMapping("/notifications")
    public String showNotifications() {
        return "notification/notificationView";
    }
}
