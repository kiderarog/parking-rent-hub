package com.parkrenthub.parking_rent_hub.services;

import com.parkrenthub.parking_rent_hub.dto.ResponseDTO;
import com.parkrenthub.parking_rent_hub.models.Spot;
import com.parkrenthub.parking_rent_hub.repositories.SpotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class SpotService {
    private final SpotRepository spotRepository;

    public SpotService(SpotRepository spotRepository) {
        this.spotRepository = spotRepository;
    }

    // Метод для проверки, находится ли машина на парковке.
    // Используется для приведения в действия механизма начисления денежной просрочки (штраф с баланса пользователя)
    // В случае, если срок бронирования уже истек, но автомобиль до сих пор стоит на парковочном месте.
    @Transactional
    public ResponseDTO checkSpotOccupancy(UUID clientId, boolean isEntering) {
        Optional<Spot> optionalSpot = spotRepository.findByClientId(clientId);
        if (optionalSpot.isEmpty()) {
            return new ResponseDTO("error", "У пользователя нет забронированных мест");
        }
        Spot spot = optionalSpot.get();
        if (isEntering) {
            if (spot.getCarOnSpotStatus()) {
                return new ResponseDTO("error", "Машина уже на парковке.");
            }
            spot.setCarOnSpotStatus(true);
            spotRepository.save(spot);
            return new ResponseDTO("success", "Пользователь заехал на парковку.");
        } else {
            if (!spot.getCarOnSpotStatus()) {
                return new ResponseDTO("error", "Машина уже выехала с парковки.");
            }
            spot.setCarOnSpotStatus(false);
            spotRepository.save(spot);
            return new ResponseDTO("success", "Пользователь выехал с парковки.");
        }
    }
}


