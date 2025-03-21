package com.parkrenthub.parking_rent_hub.exception;

import com.parkrenthub.parking_rent_hub.dto.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DuplicateKeyException;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    private ResponseEntity<ClientErrorResponse> handleDuplicateKeyException(DuplicateKeyException e) {
        ClientErrorResponse response = new ClientErrorResponse(
                "Клиент с таким username, email или номером телефона уже существует!",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }


    @ExceptionHandler
    private ResponseEntity<ClientErrorResponse> handleOtpValidationException(OtpValidationException e) {
        ClientErrorResponse response = new ClientErrorResponse(
                "Введены неверные данные для сброса пароля.",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ResponseDTO> handleUserNotFound(UsernameNotFoundException e) {
        return ResponseEntity.status(404).body(new ResponseDTO
                ("error", "Пользователь не найден."));
    }

}