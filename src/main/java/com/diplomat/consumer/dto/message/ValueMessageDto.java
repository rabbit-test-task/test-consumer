package com.diplomat.consumer.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValueMessageDto {
    private Long id;
    private Integer value;
}