package com.diplomat.consumer.service;

import com.diplomat.consumer.dto.response.MessageResponseDto;
import com.diplomat.consumer.model.DateTimeMessage;
import com.diplomat.consumer.model.ValueMessage;
import com.diplomat.consumer.repository.DateTimeMessageRepository;
import com.diplomat.consumer.repository.ValueMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceImplTest {

    @Mock
    private DateTimeMessageRepository dateTimeRepository;

    @Mock
    private ValueMessageRepository valueRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private MessageServiceImpl messageService;

    private OffsetDateTime testDateTime;
    private DateTimeMessage testDateTimeMessage;
    private ValueMessage testValueMessage;

    @BeforeEach
    void setUp() {
        testDateTime = OffsetDateTime.now(ZoneOffset.UTC);

        testDateTimeMessage = new DateTimeMessage();
        testDateTimeMessage.setId(1L);
        testDateTimeMessage.setDatetimeNow(testDateTime);
        testDateTimeMessage.setReceivedAt(testDateTime);

        testValueMessage = new ValueMessage();
        testValueMessage.setId(1L);
        testValueMessage.setValue(100);
        testValueMessage.setReceivedAt(testDateTime);
    }

    @Test
    void testProcessDateTimeMessage() {
        when(dateTimeRepository.save(any(DateTimeMessage.class))).thenReturn(testDateTimeMessage);

        DateTimeMessage result = messageService.processDateTimeMessage(testDateTime);

        assertNotNull(result);
        assertEquals(testDateTimeMessage.getId(), result.getId());
        assertEquals(testDateTimeMessage.getDatetimeNow(), result.getDatetimeNow());
        verify(dateTimeRepository).save(any(DateTimeMessage.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/messages"), any(MessageResponseDto.class));
    }

    @Test
    void testProcessValueMessage() {
        when(valueRepository.save(any(ValueMessage.class))).thenReturn(testValueMessage);

        ValueMessage result = messageService.processValueMessage(1L, 100);

        assertNotNull(result);
        assertEquals(testValueMessage.getId(), result.getId());
        assertEquals(testValueMessage.getValue(), result.getValue());
        verify(valueRepository).save(any(ValueMessage.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/messages"), any(MessageResponseDto.class));
    }

    @Test
    void testGetAllMessages() {
        List<DateTimeMessage> dateTimeMessages = Arrays.asList(testDateTimeMessage);
        List<ValueMessage> valueMessages = Arrays.asList(testValueMessage);

        when(dateTimeRepository.findAll()).thenReturn(dateTimeMessages);
        when(valueRepository.findAll()).thenReturn(valueMessages);

        List<MessageResponseDto> result = messageService.getAllMessages();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(dateTimeRepository).findAll();
        verify(valueRepository).findAll();
    }

    @Test
    void testGetRecentMessages() {
        List<DateTimeMessage> dateTimeMessages = Arrays.asList(testDateTimeMessage);
        List<ValueMessage> valueMessages = Arrays.asList(testValueMessage);

        when(dateTimeRepository.findTop50ByOrderByReceivedAtDesc()).thenReturn(dateTimeMessages);
        when(valueRepository.findTop50ByOrderByReceivedAtDesc()).thenReturn(valueMessages);

        List<MessageResponseDto> result = messageService.getRecentMessages(10);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(dateTimeRepository).findTop50ByOrderByReceivedAtDesc();
        verify(valueRepository).findTop50ByOrderByReceivedAtDesc();
    }

    @Test
    void testGetDateTimeMessageCount() {
        when(dateTimeRepository.count()).thenReturn(5L);

        long count = messageService.getDateTimeMessageCount();

        assertEquals(5L, count);
        verify(dateTimeRepository).count();
    }

    @Test
    void testGetValueMessageCount() {
        when(valueRepository.count()).thenReturn(10L);

        long count = messageService.getValueMessageCount();

        assertEquals(10L, count);
        verify(valueRepository).count();
    }

    @Test
    void testCleanupOldMessages() {
        OffsetDateTime cutoffDate = OffsetDateTime.now(ZoneOffset.UTC).minusDays(30);
        when(dateTimeRepository.countByReceivedAtBefore(any(OffsetDateTime.class))).thenReturn(5L);
        when(valueRepository.countByReceivedAtBefore(any(OffsetDateTime.class))).thenReturn(10L);

        messageService.cleanupOldMessages(30);

        verify(dateTimeRepository).deleteByReceivedAtBefore(any(OffsetDateTime.class));
        verify(valueRepository).deleteByReceivedAtBefore(any(OffsetDateTime.class));
    }
}