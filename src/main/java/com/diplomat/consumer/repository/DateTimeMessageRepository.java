package com.diplomat.consumer.repository;

import com.diplomat.consumer.model.DateTimeMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface DateTimeMessageRepository extends JpaRepository<DateTimeMessage, Long> {
    List<DateTimeMessage> findTop50ByOrderByReceivedAtDesc();
    long countByReceivedAtBefore(OffsetDateTime cutoffDate);
    void deleteByReceivedAtBefore(OffsetDateTime cutoffDate);
}
