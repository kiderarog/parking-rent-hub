package com.parkrenthub.parking_rent_hub.controllers;

import com.parkrenthub.parking_rent_hub.dto.ResponseDTO;
import com.parkrenthub.parking_rent_hub.security.ClientDetails;
import com.parkrenthub.parking_rent_hub.services.SpotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/spot")
public class SpotController {
    private final SpotService spotService;

    public SpotController(SpotService spotService) {
        this.spotService = spotService;
    }

    // Метод для установления флага "машина на парковке".
    // Функционал реализован в ручном режиме по кнопке, что имитирует работу стороннего оборудования
    // по наблюдению за выездом/въездом на парковку.
    @PostMapping("/use-spot")
    public ResponseEntity<ResponseDTO> toggleParkingSpot(@AuthenticationPrincipal ClientDetails clientDetails, @RequestParam boolean isEntering) {
        String clientId = clientDetails.getClientId();
        ResponseDTO response = spotService.checkSpotOccupancy(UUID.fromString(clientId), isEntering);

        if (response.getStatus().equals("error")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
