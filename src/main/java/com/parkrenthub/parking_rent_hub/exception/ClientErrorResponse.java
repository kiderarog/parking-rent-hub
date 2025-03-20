package com.parkrenthub.parking_rent_hub.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientErrorResponse {
    private String message;
    private Long timestamp;
}
