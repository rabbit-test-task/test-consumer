package com.diplomat.consumer.messaging.consumer;

import com.diplomat.consumer.exceptions.MessageProcessingException;
import com.diplomat.consumer.messaging.processor.MessageProcessorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RabbitMQConsumerTest {

    @Mock
    private MessageProcessorFactory processorFactory;

    @InjectMocks
    private RabbitMQConsumer rabbitMQConsumer;

    private Message dateTimeMessage;
    private Message valueMessage;

    @BeforeEach
    void setUp() {
        String dateTimeContent = "{\"datetime_now\":\"2025-04-21T07:22:41.012505+00:00\"}";
        dateTimeMessage = new Message(dateTimeContent.getBytes(), new MessageProperties());

        String valueContent = "{\"id\":1,\"value\":3}";
        valueMessage = new Message(valueContent.getBytes(), new MessageProperties());
    }

    @Test
    void testConsumeValidDateTimeMessage() {
        doNothing().when(processorFactory).processMessage(anyString());

        rabbitMQConsumer.consumeMessage(dateTimeMessage);

        verify(processorFactory).processMessage(anyString());
    }

    @Test
    void testConsumeValidValueMessage() {
        doNothing().when(processorFactory).processMessage(anyString());

        rabbitMQConsumer.consumeMessage(valueMessage);

        verify(processorFactory).processMessage(anyString());
    }

    @Test
    void testConsumeMessageWithProcessingException() {
        doThrow(new MessageProcessingException("Test processing error"))
                .when(processorFactory).processMessage(anyString());

        assertThrows(AmqpRejectAndDontRequeueException.class, () -> {
            rabbitMQConsumer.consumeMessage(dateTimeMessage);
        });

        verify(processorFactory).processMessage(anyString());
    }

    @Test
    void testConsumeMessageWithUnexpectedException() {
        doThrow(new RuntimeException("Unexpected error"))
                .when(processorFactory).processMessage(anyString());

        assertThrows(AmqpRejectAndDontRequeueException.class, () -> {
            rabbitMQConsumer.consumeMessage(dateTimeMessage);
        });

        verify(processorFactory).processMessage(anyString());
    }
}