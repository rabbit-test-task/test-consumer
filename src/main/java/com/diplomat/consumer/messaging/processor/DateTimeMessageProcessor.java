package com.diplomat.consumer.messaging.processor;

import java.time.OffsetDateTime;

import com.diplomat.consumer.dto.message.DateTimeMessageDto;
import com.diplomat.consumer.service.MessageService;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DateTimeMessageProcessor implements MessageProcessor<DateTimeMessageDto> {
    private final MessageService messageService;

    @Override
    public void process(DateTimeMessageDto message) {
        log.info("Processing datetime message: {}", message);
        OffsetDateTime dateTime = message.getDatetime_now();
        messageService.processDateTimeMessage(dateTime);
    }
}