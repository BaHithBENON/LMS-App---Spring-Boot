package com.lms.library.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lms.library.models.ModelNotification;
import com.lms.library.repositories.NotificationRepository;

@Service
public class NotificationService {
	
	@Autowired
    private NotificationRepository notificationRepository;

    public List<ModelNotification> findAll() {
        return notificationRepository.findAll();
    }

    public ModelNotification save(ModelNotification notification) {
        return notificationRepository.save(notification);
    }

    public ModelNotification findById(Long id) {
        return notificationRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        notificationRepository.deleteById(id);
    }
}
