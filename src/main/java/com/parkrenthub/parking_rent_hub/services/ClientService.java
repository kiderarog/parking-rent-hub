package com.parkrenthub.parking_rent_hub.services;

import com.parkrenthub.parking_rent_hub.dto.AuthClientDTO;
import com.parkrenthub.parking_rent_hub.dto.ClientDTO;
import com.parkrenthub.parking_rent_hub.dto.EditProfileDTO;
import com.parkrenthub.parking_rent_hub.dto.ResponseDTO;
import com.parkrenthub.parking_rent_hub.models.Client;
import com.parkrenthub.parking_rent_hub.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ClientService(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ClientDTO showProfile(String userName) {
        Optional<Client> optionalClient = clientRepository.findByUsername(userName);
        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();
            return new ClientDTO(
                    client.getUsername(),
                    client.getName(),
                    client.getSurname(),
                    client.getEmail(),
                    client.getPhone(),
                    client.getCarNumber(),
                    client.getBalance()
            );
        }
        throw new UsernameNotFoundException("Пользователь не найден.");
    }

    @Transactional
    public ResponseDTO editProfile(String clientName, EditProfileDTO editProfileDTO) {
        Optional<Client> optionalClient = clientRepository.findByUsername(clientName);
        if (optionalClient.isEmpty()) {
            throw new UsernameNotFoundException("Пользователь не найден.");
        }
        Client client = optionalClient.get();
        client.setName(editProfileDTO.getNewName());
        client.setSurname(editProfileDTO.getNewSurname());
        client.setCarNumber(editProfileDTO.getNewCarNumber());
        clientRepository.save(client);
        return new ResponseDTO("success", "Профиль пользователя успешно обновлен.");
    }

    // ДОБАВИТЬ В БУДУЩЕМ ПРОВЕРКУ НА ТО, ЧТО ЕСЛИ У ПОЛЬЗОВАТЕЛЯ ЕСТЬ ОНЛАЙН БРОНИ,
    // ЭТОТ МЕТОД БУДЕТ НЕДОСТУПЕН
    @Transactional
    public ResponseDTO deleteProfile(String clientName) {
        Client client = clientRepository.findByUsername(clientName)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден."));
        clientRepository.delete(client);
        return new ResponseDTO("success", "Аккаунт пользователя " +
                client.getUsername() + " успешно удален.");
    }

    @Transactional
    public ResponseDTO changePassword(AuthClientDTO authClientDTO) {
        Client client = clientRepository.findByUsername(authClientDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден."));

        if (passwordEncoder.matches(authClientDTO.getPassword(), client.getPassword())) {
            client.setPassword(passwordEncoder.encode(authClientDTO.getNewPassword()));
            clientRepository.save(client);
            return new ResponseDTO("success", "Пароль успешно изменен.");
        } else {
            return new ResponseDTO("error", "Неверный текущий пароль.");
        }
    }


}
