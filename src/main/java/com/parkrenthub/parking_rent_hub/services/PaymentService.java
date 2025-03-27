package com.parkrenthub.parking_rent_hub.services;

import com.parkrenthub.parking_rent_hub.dto.AddBalanceDTO;
import com.parkrenthub.parking_rent_hub.dto.ResponseDTO;
import com.parkrenthub.parking_rent_hub.exception.PaymentErrorException;
import com.parkrenthub.parking_rent_hub.models.Client;
import com.parkrenthub.parking_rent_hub.models.Penalty;
import com.parkrenthub.parking_rent_hub.models.Spot;
import com.parkrenthub.parking_rent_hub.repositories.ClientRepository;
import com.parkrenthub.parking_rent_hub.repositories.PenaltyRepository;
import com.parkrenthub.parking_rent_hub.repositories.SpotRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PaymentService {
    private final PenaltyRepository penaltyRepository;
    private final SpotRepository spotRepository;

    public PaymentService(PenaltyRepository penaltyRepository, SpotRepository spotRepository, ClientRepository clientRepository) {
        this.penaltyRepository = penaltyRepository;
        this.spotRepository = spotRepository;
        this.clientRepository = clientRepository;
    }

    private final ClientRepository clientRepository;
    // Импровизированное хранение номеров банковских карт и баланса
    // Для имитации пополнения баланса пользователя через платежную систему.
    Map<String, Double> bankCards = new HashMap<>(Map.of(
            "8456796344863222", 14000.0,
            "7896366642296758", 300.0,
            "2698455979358921", 7500.0
    ));


    // Метод для пополнения баланса пользователя с банковской карты.
    // Имитирует пополнение через платежную систему.
    @Transactional
    public ResponseDTO addBalance(String username, AddBalanceDTO addBalanceDTO) {
        Client client = clientRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден."));

        if (!bankCards.containsKey(addBalanceDTO.getCardNumber())
                || bankCards.get(addBalanceDTO.getCardNumber()) < addBalanceDTO.getMoney()) {
            throw new PaymentErrorException();
        }
        bankCards.put(addBalanceDTO.getCardNumber(), bankCards.get(addBalanceDTO.getCardNumber()) - addBalanceDTO.getMoney());
        client.setBalance(client.getBalance() + addBalanceDTO.getMoney());
        clientRepository.save(client);
        return new ResponseDTO("success", "Баланс успешно пополнен на " + addBalanceDTO.getMoney());
    }

    // Метод, который списывает деньги со счета пользователя при оплате бронирования.
    public void writeOffBookingCost(Client client, Integer penaltyMoneyToWriteOff) {
        client.setBalance(client.getBalance() - penaltyMoneyToWriteOff);
        clientRepository.save(client);
    }

    // Метод, который списывает деньги со счета пользователя за просрочку.
    private void writeOffMoneyPenalty(Client client, Double penaltyMoneyToWriteOff) {
        client.setBalance(client.getBalance() - penaltyMoneyToWriteOff);
        clientRepository.save(client);
    }


    // Метод, который проверяет пользователя и место на просрочку.
    // Если срок аренды уже вышел, а автомобиль до сих пор находится на парковке,
    // то пользователю начисляется штраф (списывается с баланса аккаунта)
    // денежная сумма, равная по стоимости одному дню аренды парковочного места
    // на конкретной парковке. Если баланс пользователя станет отрицательным,
    // он не сможет забрать свой автомобиль с парковки, пока не пополнит его.
    @Scheduled(cron = "${cron.expression}")
    @Transactional
    public void checkDelayForMoneyPenalty() {
        System.out.println("МЕТОД ДЛЯ ОПРОСА РАЗ В МИНУТУ"); // Временно настроено на 1 раз в минуту для тестов. Далее раз в сутки, 00:01

        List<Spot> allSpotList = spotRepository.findAll();
        List<Client> clientsForWriteOffPenalty = new ArrayList<>();
        List<Penalty> penalties = new ArrayList<>();
        for (Spot spot : allSpotList) {
            if (spot.getEndBookDate() != null && spot.getEndBookDate().isBefore(LocalDateTime.now()) && spot.getCarOnSpotStatus()) {
                Client client = clientRepository.findById(spot.getClientId()).orElseThrow();
                writeOffMoneyPenalty(client, Double.valueOf(spot.getParking().getDailyPrice()));
                clientsForWriteOffPenalty.add(client);
                Penalty penalty = new Penalty();
                penalty.setClientId(client.getId());
                penalty.setPenaltyDate(LocalDateTime.now());
                penalty.setPenaltySum(spot.getParking().getDailyPrice());
                penalties.add(penalty);
                client.setTotalPenaltySum(client.getTotalPenaltySum() + spot.getParking().getDailyPrice());
            }
            if (!clientsForWriteOffPenalty.isEmpty()) {
                clientRepository.saveAll(clientsForWriteOffPenalty);
                penaltyRepository.saveAll(penalties);
            }
        }
    }
}
