package com.parkrenthub.parking_rent_hub.controllers;

import com.parkrenthub.parking_rent_hub.dto.BookingDTO;
import com.parkrenthub.parking_rent_hub.dto.ResponseDTO;
import com.parkrenthub.parking_rent_hub.security.ClientDetails;
import com.parkrenthub.parking_rent_hub.services.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Недоступно без авторизации и JWT-аутентификации.
@RestController
@RequestMapping("/booking")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Осуществление бронирования парковочного места.
    // При бронировании проходит проверка пользователя на существование активной брони.
    // Проверяется наличие денежных средств на балансе.
    // Бронирование мест осуществляется тремя возможными способами: до конца дня, на определенные дни, на 30 дней.
    // Подробный функционал работы метода bookingParkingSpot в BookingService.
    @PostMapping("/book-spot")
    private ResponseEntity<ResponseDTO> bookParkingSpot (@AuthenticationPrincipal ClientDetails clientDetails,
                                                         @RequestBody BookingDTO bookingDTO) {
        String clientId = clientDetails.getClientId();
        ResponseDTO response = bookingService.bookingParkingSpot(clientId, bookingDTO);
        if (response.getStatus().equals("error")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

}
