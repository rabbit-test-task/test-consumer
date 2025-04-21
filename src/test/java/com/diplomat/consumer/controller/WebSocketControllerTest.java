package com.diplomat.consumer.controller;

import com.diplomat.consumer.dto.response.MessageResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class WebSocketControllerTest {

    @InjectMocks
    private WebSocketController webSocketController;

    @Test
    void testConnect() {
        String result = webSocketController.connect();

        assertEquals("Connected to WebSocket", result);
    }

    @Test
    void testTestMessage() {
        String testMessage = "example test msg";

        MessageResponseDto result = webSocketController.testMessage(testMessage);

        assertNotNull(result);
        assertEquals("TEST", result.getType());
        assertEquals(0L, result.getId());
        assertEquals(123, result.getValue());
        assertNotNull(result.getReceivedAt());
    }
}