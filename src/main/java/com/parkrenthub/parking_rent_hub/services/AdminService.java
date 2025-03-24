package com.parkrenthub.parking_rent_hub.services;

import com.parkrenthub.parking_rent_hub.dto.CreateParkingDTO;
import com.parkrenthub.parking_rent_hub.dto.PriceChangingDTO;
import com.parkrenthub.parking_rent_hub.dto.ResponseDTO;
import com.parkrenthub.parking_rent_hub.exception.ErrorCreatingParkingException;
import com.parkrenthub.parking_rent_hub.models.Parking;
import com.parkrenthub.parking_rent_hub.models.Spot;
import com.parkrenthub.parking_rent_hub.repositories.ParkingRepository;
import com.parkrenthub.parking_rent_hub.repositories.SpotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AdminService {

    private final ParkingRepository parkingRepository;
    private final SpotRepository spotRepository;

    public AdminService(ParkingRepository parkingRepository, SpotRepository spotRepository) {
        this.parkingRepository = parkingRepository;
        this.spotRepository = spotRepository;
    }

    // Метод для добавления парковки в приложение.
    // При добавлении, автоматически создается указанное количество мест,
    // и присваивается этой парковке по ее UUID.
    @Transactional
    public ResponseDTO createParking(CreateParkingDTO createParkingDTO) {
        try {
            Parking parking = new Parking();
            parking.setName(createParkingDTO.getName());
            parking.setLocation(createParkingDTO.getLocation());
            parking.setTotalSpots(createParkingDTO.getTotalSpots());
            parking.setAvailableSpots(createParkingDTO.getAvailableSpots());
            parking.setDailyPrice(createParkingDTO.getDailyPrice());
            parking.setMonthlyPrice(createParkingDTO.getMonthlyPrice());
            parkingRepository.save(parking);

            List<Spot> spots = new ArrayList<>();
            for (int i = 0; i < createParkingDTO.getTotalSpots(); i++) {
                Spot spot = new Spot();
                spot.setParking(parking);
                spots.add(spot);
            }
            spotRepository.saveAll(spots);

            return new ResponseDTO("success", "Парковка успешно добавлена.");
        } catch (ErrorCreatingParkingException e) {
            return new ResponseDTO("error", "Ошибка при создании парковки: " + e.getMessage());
        }
    }

    // Метод для удаления парковки из приложения.
    // Перед удалением проверяется на присутствие активных бронирований на парковке.
    // Если активные бронирования присутствуют - парковку удалить невозможно.
    @Transactional
    public ResponseDTO deleteParking(UUID parkingId) {
        Optional<Parking> optionalParking = parkingRepository.findById(parkingId);
        if (optionalParking.isEmpty()) {
            return new ResponseDTO("error", "Нет такой парковки.");
        }
        boolean b = spotRepository.existsByParkingIdAndActiveBookingIsTrue(parkingId);
        if (b) {
            return new ResponseDTO("error", "Удаление невозможно. На парковке имеются активные бронирования.");
        }
        parkingRepository.deleteById(parkingId);
        return new ResponseDTO("success", "Парковка удалена.");
    }

    // Метод для заморозки парковки.
    // Перед заморозкой проверяется на присутствие активных бронирований на парковке.
    // Если активные бронирования присутствуют - парковку заморозить невозможно.
    @Transactional
    public ResponseDTO freezeParking(UUID parkingId, boolean freeze) {
        Optional<Parking> optionalParking = parkingRepository.findById(parkingId);
        if (optionalParking.isEmpty()) {
            return new ResponseDTO("error", "Нет такой парковки.");
        }
        boolean activeBookings = spotRepository.existsByParkingIdAndActiveBookingIsTrue(parkingId);
        if (activeBookings) {
            return new ResponseDTO("error", "Заморозка невозможна. На парковке имеются активные бронирования.");
        }
        Parking parking = optionalParking.get();
        parking.setFreeze(freeze);
        parkingRepository.save(parking);
        String message = freeze ? "Парковка заморожена." : "Парковка разморожена.";
        return new ResponseDTO("success", message);
    }

    // Метод для обновления ценовой политики на парковке.
    // Возможность установления обновленной ежедневной или месячной цены за использование места.
    @Transactional
    public ResponseDTO changePrice(UUID parkingId, PriceChangingDTO priceChangingDTO) {
        Optional<Parking> optionalParking = parkingRepository.findById(parkingId);
        if (optionalParking.isEmpty()) {
            return new ResponseDTO("error", "Нет такой парковки.");
        }
        Parking parking = optionalParking.get();
        if (priceChangingDTO.getNewDailyPrice() != null) {
            parking.setDailyPrice(priceChangingDTO.getNewDailyPrice());
        }
        if (priceChangingDTO.getNewMonthlyPrice() != null) {
            parking.setMonthlyPrice(priceChangingDTO.getNewMonthlyPrice());
        }
        parkingRepository.save(parking);
        return new ResponseDTO("success", "Цены успешно изменены.");
    }

    // Метод для отображения статистики по конкретной парковке.
}
