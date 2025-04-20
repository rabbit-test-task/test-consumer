package com.diplomat.consumer.service;

import com.diplomat.consumer.dto.response.MessageResponseDto;
import com.diplomat.consumer.model.DateTimeMessage;
import com.diplomat.consumer.model.ValueMessage;
import com.diplomat.consumer.repository.DateTimeMessageRepository;
import com.diplomat.consumer.repository.ValueMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final DateTimeMessageRepository dateTimeRepository;
    private final ValueMessageRepository valueRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public DateTimeMessage processDateTimeMessage(OffsetDateTime dateTime) {
        log.info("Processing datetime message: {}", dateTime);
        DateTimeMessage message = new DateTimeMessage(dateTime);
        DateTimeMessage savedMessage = dateTimeRepository.save(message);

        messagingTemplate.convertAndSend("/topic/messages",
                MessageResponseDto.fromDateTimeMessage(savedMessage));

        return savedMessage;
    }

    @Override
    @Transactional
    public ValueMessage processValueMessage(Long id, Integer value) {
        log.info("Processing value message: id={}, value={}", id, value);
        ValueMessage message = new ValueMessage(id, value);
        ValueMessage savedMessage = valueRepository.save(message);
        
        messagingTemplate.convertAndSend("/topic/messages",
                MessageResponseDto.fromValueMessage(savedMessage));

        return savedMessage;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponseDto> getAllMessages() {
        List<MessageResponseDto> messages = new ArrayList<>();

        // Получаем все сообщения с датой и временем
        dateTimeRepository.findAll().forEach(message ->
                messages.add(MessageResponseDto.fromDateTimeMessage(message)));

        // Получаем все сообщения с id и value
        valueRepository.findAll().forEach(message ->
                messages.add(MessageResponseDto.fromValueMessage(message)));

        // Сортируем по времени получения (от новых к старым)
        return messages.stream()
                .sorted(Comparator.comparing(MessageResponseDto::getReceivedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponseDto> getRecentMessages(int limit) {
        List<MessageResponseDto> messages = new ArrayList<>();

        // Получаем последние сообщения с датой и временем
        dateTimeRepository.findTop50ByOrderByReceivedAtDesc().forEach(message ->
                messages.add(MessageResponseDto.fromDateTimeMessage(message)));

        // Получаем последние сообщения с id и value
        valueRepository.findTop50ByOrderByReceivedAtDesc().forEach(message ->
                messages.add(MessageResponseDto.fromValueMessage(message)));

        // Сортируем по времени получения (от новых к старым) и ограничиваем количество
        return messages.stream()
                .sorted(Comparator.comparing(MessageResponseDto::getReceivedAt).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getDateTimeMessageCount() {
        return dateTimeRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getValueMessageCount() {
        return valueRepository.count();
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupOldMessages(int daysToKeep) {
        OffsetDateTime cutoffDate = OffsetDateTime.now(ZoneOffset.UTC).minusDays(daysToKeep);

        // Удаление старых сообщений
        long datetimeDeleted = dateTimeRepository.countByReceivedAtBefore(cutoffDate);
        dateTimeRepository.deleteByReceivedAtBefore(cutoffDate);

        long valueDeleted = valueRepository.countByReceivedAtBefore(cutoffDate);
        valueRepository.deleteByReceivedAtBefore(cutoffDate);

        log.info("Cleaned up old messages: {} datetime messages and {} value messages deleted",
                datetimeDeleted, valueDeleted);
    }
}