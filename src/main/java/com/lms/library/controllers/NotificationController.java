package com.lms.library.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.library.models.ModelNotification;
import com.lms.library.services.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public List<ModelNotification> getAllNotifications() {
        return notificationService.findAll();
    }

    @PostMapping
    public ModelNotification createNotification(@RequestBody ModelNotification notification) {
        return notificationService.save(notification);
    }

    @GetMapping("/{id}")
    public ModelNotification getNotificationById(@PathVariable Long id) {
        return notificationService.findById(id);
    }

    @PutMapping("/{id}")
    public ModelNotification updateNotification(@PathVariable Long id, @RequestBody ModelNotification notificationDetails) {
        // Implémentez la logique de mise à jour  ici
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable Long id) {
        notificationService.deleteById(id);
    }
}
