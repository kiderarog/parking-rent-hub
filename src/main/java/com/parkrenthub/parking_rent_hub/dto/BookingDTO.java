package com.parkrenthub.parking_rent_hub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class BookingDTO {
    private UUID parkingId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean month;
}
