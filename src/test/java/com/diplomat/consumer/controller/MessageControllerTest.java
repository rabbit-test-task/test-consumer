package com.diplomat.consumer.controller;

import com.diplomat.consumer.dto.response.MessageResponseDto;
import com.diplomat.consumer.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class MessageControllerTest {

    @Mock
    private MessageService messageService;

    @InjectMocks
    private MessageController messageController;

    private MockMvc mockMvc;
    private MessageResponseDto dateTimeMessageDto;
    private MessageResponseDto valueMessageDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(messageController).build();

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        dateTimeMessageDto = new MessageResponseDto();
        dateTimeMessageDto.setType("DATETIME");
        dateTimeMessageDto.setId(1L);
        dateTimeMessageDto.setDateTime(now);
        dateTimeMessageDto.setReceivedAt(now);

        valueMessageDto = new MessageResponseDto();
        valueMessageDto.setType("VALUE");
        valueMessageDto.setId(2L);
        valueMessageDto.setValue(200);
        valueMessageDto.setReceivedAt(now);
    }

    @Test
    void testGetAllMessages() throws Exception {
        List<MessageResponseDto> messages = Arrays.asList(dateTimeMessageDto, valueMessageDto);
        when(messageService.getAllMessages()).thenReturn(messages);

        mockMvc.perform(get("/api/messages")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].type", is("DATETIME")))
                .andExpect(jsonPath("$[1].type", is("VALUE")));

        verify(messageService).getAllMessages();
    }

    @Test
    void testGetRecentMessages() throws Exception {
        List<MessageResponseDto> messages = Arrays.asList(dateTimeMessageDto, valueMessageDto);
        when(messageService.getRecentMessages(20)).thenReturn(messages);

        mockMvc.perform(get("/api/messages/recent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].type", is("DATETIME")))
                .andExpect(jsonPath("$[1].type", is("VALUE")));

        verify(messageService).getRecentMessages(20);
    }

    @Test
    void testGetRecentMessagesWithCustomLimit() throws Exception {
        List<MessageResponseDto> messages = Arrays.asList(dateTimeMessageDto);
        when(messageService.getRecentMessages(5)).thenReturn(messages);

        mockMvc.perform(get("/api/messages/recent")
                .param("limit", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type", is("DATETIME")));

        verify(messageService).getRecentMessages(5);
    }

    @Test
    void testGetStats() throws Exception {
        when(messageService.getDateTimeMessageCount()).thenReturn(5L);
        when(messageService.getValueMessageCount()).thenReturn(10L);

        mockMvc.perform(get("/api/messages/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datetimeMessageCount", is(5)))
                .andExpect(jsonPath("$.valueMessageCount", is(10)))
                .andExpect(jsonPath("$.totalCount", is(15)));

        verify(messageService, times(2)).getDateTimeMessageCount();
        verify(messageService, times(2)).getValueMessageCount();
    }

    @Test
    void testTriggerCleanup() throws Exception {
        doNothing().when(messageService).cleanupOldMessages(30);

        mockMvc.perform(post("/api/messages/cleanup")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.message", containsString("30 days")));

        verify(messageService).cleanupOldMessages(30);
    }

    @Test
    void testTriggerCleanupWithCustomDays() throws Exception {
        doNothing().when(messageService).cleanupOldMessages(7);

        mockMvc.perform(post("/api/messages/cleanup")
                .param("daysToKeep", "7")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.message", containsString("7 days")));

        verify(messageService).cleanupOldMessages(7);
    }
}