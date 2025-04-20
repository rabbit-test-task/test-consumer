package com.diplomat.consumer.repository;

import com.diplomat.consumer.model.ValueMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface ValueMessageRepository extends JpaRepository<ValueMessage, Long> {
    List<ValueMessage> findTop50ByOrderByReceivedAtDesc();
    long countByReceivedAtBefore(OffsetDateTime cutoffDate);
    void deleteByReceivedAtBefore(OffsetDateTime cutoffDate);
}