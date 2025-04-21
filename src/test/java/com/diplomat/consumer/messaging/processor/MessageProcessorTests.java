package com.diplomat.consumer.messaging.processor;

import com.diplomat.consumer.dto.message.DateTimeMessageDto;
import com.diplomat.consumer.dto.message.ValueMessageDto;
import com.diplomat.consumer.exceptions.MessageProcessingException;
import com.diplomat.consumer.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageProcessorTests {

    @Mock
    private MessageService messageService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DateTimeMessageProcessor dateTimeMessageProcessor;

    @InjectMocks
    private ValueMessageProcessor valueMessageProcessor;

    @Mock
    private MessageProcessorFactory messageProcessorFactory;

    private DateTimeMessageDto dateTimeMessageDto;
    private ValueMessageDto valueMessageDto;
    private OffsetDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = OffsetDateTime.now(ZoneOffset.UTC);

        dateTimeMessageDto = new DateTimeMessageDto();
        dateTimeMessageDto.setDatetime_now(testDateTime);

        valueMessageDto = new ValueMessageDto();
        valueMessageDto.setId(1L);
        valueMessageDto.setValue(100);

        messageProcessorFactory = new MessageProcessorFactory(objectMapper, dateTimeMessageProcessor, valueMessageProcessor);
    }

    @Test
    void testDateTimeMessageProcessing() {
        when(messageService.processDateTimeMessage(any(OffsetDateTime.class))).thenReturn(null);

        dateTimeMessageProcessor.process(dateTimeMessageDto);

        verify(messageService).processDateTimeMessage(eq(testDateTime));
    }

    @Test
    void testValueMessageProcessing() {
        when(messageService.processValueMessage(anyLong(), anyInt())).thenReturn(null);

        valueMessageProcessor.process(valueMessageDto);

        verify(messageService).processValueMessage(eq(1L), eq(100));
    }

    @Test
    void testProcessDateTimeMessage() throws Exception {
        MessageProcessorFactory factory = mock(MessageProcessorFactory.class);
        DateTimeMessageProcessor dtProcessor = mock(DateTimeMessageProcessor.class);
        ValueMessageProcessor vProcessor = mock(ValueMessageProcessor.class);

        // Fix Java 8 date/time type `java.time.OffsetDateTime` not supported by default
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        MessageProcessorFactory realFactory = new MessageProcessorFactory(mapper, dtProcessor, vProcessor);

        String dateTimeMessage = "{\"datetime_now\":\"2025-04-21T07:22:41.012505+00:00\"}";

        realFactory.processMessage(dateTimeMessage);

        verify(dtProcessor).process(any(DateTimeMessageDto.class));
        verifyNoInteractions(vProcessor);
    }

    @Test
    void testProcessValueMessage() throws Exception {
        DateTimeMessageProcessor dtProcessor = mock(DateTimeMessageProcessor.class);
        ValueMessageProcessor vProcessor = mock(ValueMessageProcessor.class);
        ObjectMapper mapper = new ObjectMapper();

        MessageProcessorFactory realFactory = new MessageProcessorFactory(mapper, dtProcessor, vProcessor);

        String valueMessage = "{\"id\":1,\"value\":1}";

        realFactory.processMessage(valueMessage);

        verify(vProcessor).process(any(ValueMessageDto.class));
        verifyNoInteractions(dtProcessor);
    }

    @Test
    void testProcessInvalidMessage() {
        DateTimeMessageProcessor dtProcessor = mock(DateTimeMessageProcessor.class);
        ValueMessageProcessor vProcessor = mock(ValueMessageProcessor.class);
        ObjectMapper mapper = new ObjectMapper();

        MessageProcessorFactory realFactory = new MessageProcessorFactory(mapper, dtProcessor, vProcessor);

        String invalidMessage = "{\"some_field\":\"some_value\"}";

        assertThrows(MessageProcessingException.class, () -> {
            realFactory.processMessage(invalidMessage);
        });

        verifyNoInteractions(dtProcessor);
        verifyNoInteractions(vProcessor);
    }

    @Test
    void testProcessInvalidJson() {
        DateTimeMessageProcessor dtProcessor = mock(DateTimeMessageProcessor.class);
        ValueMessageProcessor vProcessor = mock(ValueMessageProcessor.class);
        ObjectMapper mapper = new ObjectMapper();

        MessageProcessorFactory realFactory = new MessageProcessorFactory(mapper, dtProcessor, vProcessor);

        String invalidJson = "{ invalid json }";

        assertThrows(MessageProcessingException.class, () -> {
            realFactory.processMessage(invalidJson);
        });

        verifyNoInteractions(dtProcessor);
        verifyNoInteractions(vProcessor);
    }
}