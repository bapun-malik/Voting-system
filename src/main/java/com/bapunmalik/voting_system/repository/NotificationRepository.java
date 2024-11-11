package com.bapunmalik.voting_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bapunmalik.voting_system.models.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {
    
}
