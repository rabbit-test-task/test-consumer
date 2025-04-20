package com.diplomat.consumer.messaging.processor;

public interface MessageProcessor<T> {
    void process(T message);
}