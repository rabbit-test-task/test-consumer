package com.diplomat.consumer.controller;

import com.diplomat.consumer.dto.response.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    @MessageMapping("/messages.connect")
    @SendTo("/topic/status")
    public String connect() {
        log.info("WebSocket client connected");
        return "Connected to WebSocket";
    }

    @MessageMapping("/messages.test")
    @SendTo("/topic/messages")
    public MessageResponseDto testMessage(String message) {
        log.info("Test message received: {}", message);
        MessageResponseDto testMessage = new MessageResponseDto();
        testMessage.setType("TEST");
        testMessage.setId(0L);
        testMessage.setValue(123);
        testMessage.setReceivedAt(OffsetDateTime.now(ZoneOffset.UTC));
        return testMessage;
    }
}
