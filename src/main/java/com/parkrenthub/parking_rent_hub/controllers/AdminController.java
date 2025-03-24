package com.parkrenthub.parking_rent_hub.controllers;

import com.parkrenthub.parking_rent_hub.dto.CreateParkingDTO;
import com.parkrenthub.parking_rent_hub.dto.PriceChangingDTO;
import com.parkrenthub.parking_rent_hub.dto.ResponseDTO;
import com.parkrenthub.parking_rent_hub.services.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

// Все ручки AdminController доступны только пользователям с правами ADMIN.
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // Добавление парковки в приложение.
    @PostMapping("/create-parking")
    public ResponseEntity<ResponseDTO> addParking(@RequestBody CreateParkingDTO createParkingDTO) {
        ResponseDTO response = adminService.createParking(createParkingDTO);
        if (response.getStatus().equals("error")) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Удаление парковки из приложения.
    // Доступно только при отсутствии активных бронирований у пользователей на парковке.
    @PostMapping("/delete-parking/{parkingId}")
    public ResponseEntity<ResponseDTO> deleteParking(@PathVariable("parkingId") UUID parkingId) {
        ResponseDTO response = adminService.deleteParking(parkingId);
        if (response.getStatus().equals("error")) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Заморозка парковки.
    // Доступно только при отсутствии активных бронирований у пользователей на парковке.
    @PostMapping("/freeze-parking/{parkingId}")
    public ResponseEntity<ResponseDTO> freezeParking(@PathVariable("parkingId") UUID parkingId,
                                                     @RequestParam("freeze") boolean freeze) {
        ResponseDTO response = adminService.freezeParking(parkingId, freeze);
        if (response.getStatus().equals("error")) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Изменение ценовой политики парковки.
    @PostMapping("/change-price/{parkingId}")
    public ResponseEntity<ResponseDTO> changePrice(@PathVariable("parkingId") UUID parkingId, PriceChangingDTO priceChangingDTO) {
        ResponseDTO response = adminService.changePrice(parkingId, priceChangingDTO);
        if (response.getStatus().equals("error")) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
