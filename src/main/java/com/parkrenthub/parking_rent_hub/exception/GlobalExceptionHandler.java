package com.parkrenthub.parking_rent_hub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DuplicateKeyException;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    private ResponseEntity<ErrorResponse> handleDuplicateKeyException(DuplicateKeyException e) {
        ErrorResponse response = new ErrorResponse(
                "Клиент с таким username, email или номером телефона уже существует!",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }


    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleOtpValidationException(OtpValidationException e) {
        ErrorResponse response = new ErrorResponse(
                "Введены неверные данные для сброса пароля.",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UsernameNotFoundException e) {
        ErrorResponse response = new ErrorResponse(
                "Пользователь не найден.",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(PaymentErrorException.class)
    public ResponseEntity<ErrorResponse> handlePaymentErrorException(PaymentErrorException e) {
        ErrorResponse response = new ErrorResponse(
                "Ошибка платежного сервиса",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(ErrorCreatingParkingException.class)
    public ResponseEntity<ErrorResponse> handleErrorCreatingParkingException(ErrorCreatingParkingException e) {
        ErrorResponse response = new ErrorResponse(
                "При создании парковки произошла ошибка.",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }



}