package com.diplomat.consumer.dto.response;

import com.diplomat.consumer.model.DateTimeMessage;
import com.diplomat.consumer.model.ValueMessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseDto {

    private String type;  // "DATETIME" или "VALUE"
    private Long id;
    private OffsetDateTime dateTime;
    private Integer value;
    private OffsetDateTime receivedAt;

    // Создание DTO из DateTimeMessage
    public static MessageResponseDto fromDateTimeMessage(DateTimeMessage message) {
        MessageResponseDto dto = new MessageResponseDto();
        dto.setType("DATETIME");
        dto.setId(message.getId());
        dto.setDateTime(message.getDatetimeNow());
        dto.setReceivedAt(message.getReceivedAt());
        return dto;
    }

    // Создание DTO из ValueMessage
    public static MessageResponseDto fromValueMessage(ValueMessage message) {
        MessageResponseDto dto = new MessageResponseDto();
        dto.setType("VALUE");
        dto.setId(message.getId());
        dto.setValue(message.getValue());
        dto.setReceivedAt(message.getReceivedAt());
        return dto;
    }
}