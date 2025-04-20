package com.diplomat.consumer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class ValueMessage {

    @Id
    private Long id;

    @Column(name = "`value`") // экранирование, чтобы избежать конфликта
    private Integer value;

    private OffsetDateTime receivedAt;

    public ValueMessage(Long id, Integer value) {
        this.id = id;
        this.value = value;
        this.receivedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }
}
