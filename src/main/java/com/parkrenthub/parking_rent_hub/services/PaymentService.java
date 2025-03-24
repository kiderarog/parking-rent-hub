package com.parkrenthub.parking_rent_hub.services;

import com.parkrenthub.parking_rent_hub.dto.AddBalanceDTO;
import com.parkrenthub.parking_rent_hub.dto.ResponseDTO;
import com.parkrenthub.parking_rent_hub.exception.PaymentErrorException;
import com.parkrenthub.parking_rent_hub.models.Client;
import com.parkrenthub.parking_rent_hub.repositories.ClientRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    private final ClientRepository clientRepository;
    // Импровизированное хранение номеров банковских карт и баланса
    // Для имитации пополнения баланса пользователя через платежную систему.
    Map<String, Double> bankCards = new HashMap<>(Map.of(
            "8456796344863222", 14000.0,
            "7896366642296758", 300.0,
            "2698455979358921", 7500.0
    ));

    public PaymentService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

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

    // Нужно добавить метод, который будет списывать деньги со счета пользователя при бронировании места



}
