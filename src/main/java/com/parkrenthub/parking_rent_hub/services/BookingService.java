package com.parkrenthub.parking_rent_hub.services;

import com.parkrenthub.parking_rent_hub.dto.BookingDTO;
import com.parkrenthub.parking_rent_hub.dto.ResponseDTO;
import com.parkrenthub.parking_rent_hub.models.Client;
import com.parkrenthub.parking_rent_hub.models.Parking;
import com.parkrenthub.parking_rent_hub.models.Spot;
import com.parkrenthub.parking_rent_hub.repositories.ClientRepository;
import com.parkrenthub.parking_rent_hub.repositories.ParkingRepository;
import com.parkrenthub.parking_rent_hub.repositories.SpotRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class BookingService {
    private final ClientRepository clientRepository;
    private final SpotRepository spotRepository;
    private final ParkingRepository parkingRepository;
    private final PaymentService paymentService;
    private final PyrusService pyrusService;

    public BookingService(ClientRepository clientRepository, SpotRepository spotRepository, ParkingRepository parkingRepository, PaymentService paymentService, PyrusService pyrusService) {
        this.clientRepository = clientRepository;
        this.spotRepository = spotRepository;
        this.parkingRepository = parkingRepository;
        this.paymentService = paymentService;
        this.pyrusService = pyrusService;
    }


    // Метод для бронирования парковочного места.
    // Происходит проверка на наличие свободного места на выбранной парковке.
    // Если место есть, а у пользователя нет активных бронирований, ему предлагается забронировать место
    // На парковке на: 1) до конца дня; 2) на определенные дни; 3) на 30 дней.
    // В зависимости от выбранных дат бронирования, в действие приводится 1 из 3 private методов.
    // При успешном бронировании в бронирование также добавляется в CRM -> 'Бронирования'
    @Transactional
    public ResponseDTO bookingParkingSpot(String clientIdString, BookingDTO bookingDTO) {
        UUID clientId = UUID.fromString(clientIdString);

        Optional<Spot> optionalSpot = spotRepository.findFirstByParkingIdAndActiveBookingIsFalse(bookingDTO.getParkingId());
        if (optionalSpot.isEmpty()) {
            return new ResponseDTO("error", "На этой парковке нет свободных мест.");
        }
        Optional<Client> optionalClient = clientRepository.findById(clientId);
        if (optionalClient.isEmpty()) {
            throw new EntityNotFoundException("Нет такого пользователя.");
        }
        Optional<Parking> optionalParking = parkingRepository.findById(bookingDTO.getParkingId());
        if (optionalParking.isEmpty()) {
            throw new EntityNotFoundException("Парковка не найдена.");
        }

        Spot randomSpot = optionalSpot.get();
        Client client = optionalClient.get();
        Parking parking = optionalParking.get();

        if (client.getBookedSpotId() != null) {
            return new ResponseDTO("error", "У пользователя уже имеется активное бронирование");
        }

        ResponseDTO response = null;

        if (bookingDTO.isMonth()) {
            response = bookForMonth(parking, randomSpot, client, bookingDTO);
        } else if (bookingDTO.getStartDate() != null && bookingDTO.getEndDate() == null) {
            response = bookToTheEndOfDay(parking, randomSpot, client);
        } else if (bookingDTO.getStartDate() != null && bookingDTO.getEndDate() != null) {
            response = bookForSomeDays(parking, randomSpot, client, bookingDTO);
        }

        if (response != null && response.getStatus().equals("success")) {
            spotRepository.save(randomSpot);
            clientRepository.save(client);
            parkingRepository.save(parking);
            pyrusService.addBookingCRM(randomSpot);

        }

        return response != null ? response : new ResponseDTO("error", "Непредвиденная ошибка при бронировании.");
    }

    // Метод для инициализации процесса бронирования парковочного места на 30 дней
    private ResponseDTO bookForMonth(Parking parking, Spot spot, Client client, BookingDTO bookingDTO) {
        if (client.getBalance() >= parking.getMonthlyPrice()) {
            spot.setClientId(client.getId());
            spot.setStartBookDate(bookingDTO.getStartDate().toLocalDate().atStartOfDay());
            spot.setEndBookDate(spot.getStartBookDate().plusDays(30));
            spot.setCarNumber(client.getCarNumber());
            spot.setActiveBooking(true);
            client.setBookedSpotId(spot.getId());
            paymentService.writeOffBookingCost(client, parking.getMonthlyPrice());
            parking.setBookedSpots(parking.getBookedSpots() + 1);

            return new ResponseDTO("success", "Место на парковке забронировано на 30 дней.");
        }
        return new ResponseDTO("error", "Недостаточно средств для бронирования на месяц.");
    }

    // Метод для инициализации процесса бронирования парковочного места до конца дня
    private ResponseDTO bookToTheEndOfDay(Parking parking, Spot spot, Client client) {
        if (client.getBalance() >= parking.getDailyPrice()) {
            spot.setClientId(client.getId());
            spot.setStartBookDate(LocalDateTime.now());
            spot.setEndBookDate(spot.getStartBookDate().plusDays(1).toLocalDate().atStartOfDay());
            spot.setCarNumber(client.getCarNumber());
            spot.setActiveBooking(true);
            client.setBookedSpotId(spot.getId());
            paymentService.writeOffBookingCost(client, parking.getDailyPrice());
            parking.setBookedSpots(parking.getBookedSpots() + 1);

            return new ResponseDTO("success", "Место забронировано до конца дня.");
        }
        return new ResponseDTO("error", "Недостаточно средств для бронирования до конца дня.");
    }

    // Метод для инициализации процесса бронирования парковочного места на определенные дни
    private ResponseDTO bookForSomeDays(Parking parking, Spot spot, Client client, BookingDTO bookingDTO) {
        spot.setClientId(client.getId());
        spot.setStartBookDate(bookingDTO.getStartDate().toLocalDate().atStartOfDay());
        spot.setEndBookDate(bookingDTO.getEndDate().toLocalDate().atStartOfDay());
        spot.setCarNumber(client.getCarNumber());
        client.setBookedSpotId(spot.getId());

        long days = ChronoUnit.DAYS.between(bookingDTO.getStartDate(), bookingDTO.getEndDate());
        if (client.getBalance() >= parking.getDailyPrice() * days) {
            paymentService.writeOffBookingCost(client, (int) (parking.getDailyPrice() * days));
            parking.setBookedSpots(parking.getBookedSpots() + 1);

            return new ResponseDTO("success", "Место забронировано на " + days + " дней.");
        }
        return new ResponseDTO("error", "Недостаточно средств для бронирования на выбранный период.");
    }

    // Метод, который освобождает место в системе после окончания срока бронирования.
    // РАБОТАЕТ ТОЛЬКО В ТОМ СЛУЧАЕ, ЕСЛИ ПРИ ОКОНЧЕНИИ СРОКА БРОНИРОВАНИЯ
    // АВТОМОБИЛЬ ПОЛЬЗОВАТЕЛЯ НЕ СТОИТ НА ПАРКОВОЧНОМ МЕСТЕ.
    // В противном случае - срабатывает метод из PaymentService для начисления штрафа за просрочку.
    @Scheduled(cron = "${cron.expression}")
    @Transactional
    public void releaseSpots() {
        System.out.println("МЕТОД ДЛЯ ОПРОСА РАЗ В МИНУТУ"); // Настроено для теста на 1 минуту, затем раз в сутки в 00:01
        List<Spot> allSpotList = spotRepository.findAll();
        List<Spot> spotsForRelease = new ArrayList<>();
        List<Client> clientsForRelease = new ArrayList<>();
        for (Spot spot : allSpotList) {
            if (spot.getEndBookDate() != null && spot.getEndBookDate().isBefore(LocalDateTime.now()) && !spot.getCarOnSpotStatus()) {
                UUID clientId = spot.getClientId();
                spot.setClientId(null);
                spot.setStartBookDate(null);
                spot.setEndBookDate(null);
                spot.setCarNumber(null);
                spot.setActiveBooking(false);
                spotsForRelease.add(spot);
                spot.getParking().setBookedSpots(spot.getParking().getBookedSpots() -1);
                if (clientId != null) {
                    Client client = clientRepository.findById(clientId).orElseThrow();
                    client.setBookedSpotId(null);
                    clientsForRelease.add(client);
                }
            }
        }
        if (!clientsForRelease.isEmpty()) {
            clientRepository.saveAll(clientsForRelease);
        }
        if (!spotsForRelease.isEmpty()) {
            spotRepository.saveAll(spotsForRelease);
        }
    }
}
