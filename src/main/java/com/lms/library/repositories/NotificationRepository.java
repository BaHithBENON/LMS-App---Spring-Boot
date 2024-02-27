package com.lms.library.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.library.models.ModelNotification;

@Repository
public interface NotificationRepository extends JpaRepository<ModelNotification, Long> {

}