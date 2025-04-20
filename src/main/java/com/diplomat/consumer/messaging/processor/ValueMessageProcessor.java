package com.diplomat.consumer.messaging.processor;

import com.diplomat.consumer.dto.message.ValueMessageDto;
import com.diplomat.consumer.service.MessageService;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValueMessageProcessor implements MessageProcessor<ValueMessageDto> {
    private final MessageService messageService;

    @Override
    public void process(ValueMessageDto message) {
        log.info("Processing value message: id={}, value={}", message.getId(), message.getValue());
        messageService.processValueMessage(message.getId(), message.getValue());
    }
}
