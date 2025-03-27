package com.parkrenthub.parking_rent_hub.controllers;

import com.parkrenthub.parking_rent_hub.dto.ParkingInfoDTO;
import com.parkrenthub.parking_rent_hub.services.ParkingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/parking")
public class ParkingController {
    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    // Метод для передачи списка всех парковок в формате JSON во фронтенд,
    // для реализации листа парковок для бронирования.
    @GetMapping("/show-all")
    public ResponseEntity<List<ParkingInfoDTO>> showAllParkingPlaces() {
        return ResponseEntity.ok(parkingService.getAllParkingSpaces());

    }
}
