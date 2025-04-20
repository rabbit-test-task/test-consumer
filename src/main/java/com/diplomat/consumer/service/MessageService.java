package com.diplomat.consumer.service;

import com.diplomat.consumer.dto.response.MessageResponseDto;
import com.diplomat.consumer.model.DateTimeMessage;
import com.diplomat.consumer.model.ValueMessage;

import java.time.OffsetDateTime;
import java.util.List;

public interface MessageService {

    DateTimeMessage processDateTimeMessage(OffsetDateTime dateTime);
    ValueMessage processValueMessage(Long id, Integer value);

    List<MessageResponseDto> getAllMessages();
    List<MessageResponseDto> getRecentMessages(int limit);

    long getDateTimeMessageCount();
    long getValueMessageCount();

    void cleanupOldMessages(int daysToKeep);
}