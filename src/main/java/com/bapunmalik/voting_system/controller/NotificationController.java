package com.bapunmalik.voting_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.bapunmalik.voting_system.models.Notification;
import com.bapunmalik.voting_system.service.NotificationService;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notifications")
    public String showNotificationPage(Model model) {
        model.addAttribute("notification", new Notification()); // to display form
        model.addAttribute("notifications", notificationService.getAllNotifications()); // to display notifications list
        return "notification";
    }

    @PostMapping("/admin/add-notification")
    public String addNotification(Notification notification, Model model) {
        notificationService.addNotification(notification);
        return "redirect:/notifications"; // redirect to the notification page to show updated list
    }
}