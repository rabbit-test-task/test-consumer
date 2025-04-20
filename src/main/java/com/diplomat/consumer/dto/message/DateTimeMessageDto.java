package com.diplomat.consumer.dto.message;


import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateTimeMessageDto {
    private OffsetDateTime datetime_now;
}
