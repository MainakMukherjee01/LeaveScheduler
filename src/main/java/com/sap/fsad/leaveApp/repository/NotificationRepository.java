package com.sap.fsad.leaveApp.repository;

import com.sap.fsad.leaveApp.model.Notification;
import com.sap.fsad.leaveApp.model.User;
import com.sap.fsad.leaveApp.model.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser(User user);

    List<Notification> findByUserAndIsReadFalse(User user);

    List<Notification> findByUserAndType(User user, NotificationType type);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    long countUnreadNotificationsByUserId(Long userId);

    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    List<Notification> findByRelatedEntityId(Long entityId);
}
