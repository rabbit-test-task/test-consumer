package com.diplomat.consumer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateTimeMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private OffsetDateTime datetimeNow;
    private OffsetDateTime receivedAt;

    public DateTimeMessage(OffsetDateTime datetimeNow) {
        this.datetimeNow = datetimeNow;
        this.receivedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }
}
