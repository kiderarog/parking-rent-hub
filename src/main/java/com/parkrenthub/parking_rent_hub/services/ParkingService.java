package com.parkrenthub.parking_rent_hub.services;

import com.parkrenthub.parking_rent_hub.dto.ParkingInfoDTO;
import com.parkrenthub.parking_rent_hub.models.Parking;
import com.parkrenthub.parking_rent_hub.repositories.ParkingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParkingService {
    private final ParkingRepository parkingRepository;

    @Autowired
    public ParkingService(ParkingRepository parkingRepository) {
        this.parkingRepository = parkingRepository;
    }

    // Метод, который отдает список всех парковок для отображения на frontend.
    @Transactional(readOnly = true)
    public List<ParkingInfoDTO> getAllParkingSpaces() {
        List<Parking> parkingSpaces = parkingRepository.findAll();
        return parkingSpaces.stream()
                .map(p -> new ParkingInfoDTO(
                        p.getName(),
                        p.getLocation(),
                        p.getTotalSpots(),
                        p.getAvailableSpots(),
                        p.getDailyPrice(),
                        p.getMonthlyPrice()
                ))
                .collect(Collectors.toList());
    }
}
