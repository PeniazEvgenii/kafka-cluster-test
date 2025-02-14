package by.itacademy.ws.emailNotificationMicroservice.repository.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProcessedRepository extends JpaRepository<ProcessedEventEntity, Long> {

    Optional<ProcessedEventEntity> findByMessageId(String messageId);
}
