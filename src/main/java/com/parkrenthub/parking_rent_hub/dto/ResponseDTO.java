package com.parkrenthub.parking_rent_hub.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // Исключаем поля с null
public class ResponseDTO {
    private String status;
    private String message;
    private List<String> errors;

    public ResponseDTO(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public ResponseDTO(String status, List<String> errors) {
        this.status = status;
        this.errors = errors;
    }
}

