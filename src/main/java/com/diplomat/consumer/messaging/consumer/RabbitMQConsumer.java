package com.diplomat.consumer.messaging.consumer;

import com.diplomat.consumer.exceptions.MessageProcessingException;
import com.diplomat.consumer.messaging.processor.MessageProcessorFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQConsumer {
    private final MessageProcessorFactory processorFactory;

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void consumeMessage(Message message) {
        try {
            String messageContent = new String(message.getBody());
            log.info("Received message: {}", messageContent);
            processorFactory.processMessage(messageContent);
        } catch (MessageProcessingException e) {
            log.error("Error processing message: {}", e.getMessage(), e);
            throw new AmqpRejectAndDontRequeueException("Failed to process message", e);
        } catch (Exception e) {
            log.error("Unexpected error processing message: {}", e.getMessage(), e);
            throw new AmqpRejectAndDontRequeueException("Unexpected error", e);
        }
    }
}
