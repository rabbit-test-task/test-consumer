package com.diplomat.consumer.messaging.processor;

import java.io.IOException;

import com.diplomat.consumer.dto.message.DateTimeMessageDto;
import com.diplomat.consumer.dto.message.ValueMessageDto;
import com.diplomat.consumer.exceptions.MessageProcessingException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageProcessorFactory {
    private final ObjectMapper objectMapper;
    private final DateTimeMessageProcessor dateTimeProcessor;
    private final ValueMessageProcessor valueProcessor;

    public void processMessage(String messageContent) {
        try {
            JsonNode root = objectMapper.readTree(messageContent);

            if (root.has("datetime_now")) {
                DateTimeMessageDto dto = objectMapper.treeToValue(root, DateTimeMessageDto.class);
                dateTimeProcessor.process(dto);
            } else if (root.has("id") && root.has("value")) {
                ValueMessageDto dto = objectMapper.treeToValue(root, ValueMessageDto.class);
                valueProcessor.process(dto);
            } else {
                throw new MessageProcessingException("Unknown message format: " + messageContent);
            }
        } catch (IOException e) {
            log.error("Error parsing JSON message: {}", messageContent, e);
            throw new MessageProcessingException("Failed to parse message: " + e.getMessage(), e);
        }
    }
}
