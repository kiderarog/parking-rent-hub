package com.parkrenthub.parking_rent_hub.services;

import com.parkrenthub.parking_rent_hub.dto.AuthClientDTO;
import com.parkrenthub.parking_rent_hub.dto.ResponseDTO;
import com.parkrenthub.parking_rent_hub.exception.OtpValidationException;
import com.parkrenthub.parking_rent_hub.models.Client;
import com.parkrenthub.parking_rent_hub.email.EmailSender;
import com.parkrenthub.parking_rent_hub.onetimepassword.OtpService;
import com.parkrenthub.parking_rent_hub.repositories.ClientRepository;
import com.parkrenthub.parking_rent_hub.security.JWTUtil;
import com.parkrenthub.parking_rent_hub.security.Roles;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final JWTUtil jwtUtil;
    private final EmailSender emailSender;
    private final OtpService otpService;

    @Autowired
    public AuthService(ClientRepository clientRepository, PasswordEncoder passwordEncoder,
                       ModelMapper modelMapper, JWTUtil jwtUtil, EmailSender emailSender,
                       OtpService otpService) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.jwtUtil = jwtUtil;
        this.emailSender = emailSender;
        this.otpService = otpService;
    }

    @Transactional
    public void saveClient(AuthClientDTO authClientDTO) {
        Client client = modelMapper.map(authClientDTO, Client.class);
        client.setPassword(passwordEncoder.encode(client.getPassword()));
        client.setRole(Roles.ROLE_USER);
        clientRepository.save(client);
    }

    @Transactional
    public ResponseDTO getAuthorizationToken(AuthClientDTO authClientDTO) {
        Optional<Client> optionalClient =
                clientRepository.findByUsername(authClientDTO.getUsername());
        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();
            if (passwordEncoder.matches(authClientDTO.getPassword(), client.getPassword())) {
                return new ResponseDTO("token", jwtUtil.generateToken(client.getUsername(), client.getRole().name()));
            }
        }
        return new ResponseDTO("error", "Неверные имя пользователя или пароль.");

    }


    @Transactional
    public Optional<Client> findClientByEmail(String email) {
        return clientRepository.findByEmail(email);
    }


    @Transactional
    public ResponseDTO resetPasswordRequestProcessing(String email) {
        Optional<Client> optionalClient =
                findClientByEmail(email);
        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();
            otpService.addOtp(email);
            emailSender.sendOtp(client);
            return new ResponseDTO("success", "Код восстановления отправлен на Ваш Email-адрес.");
        }
        return new ResponseDTO("error", "Пользователь с таким Email не найден");
    }

    @Transactional
    public ResponseDTO replaceForgottenPassword(Integer otpCode, String newPassword) {
        Optional<String> optionalEmail = otpService.findClientEmailByOtp(otpCode);

        if (optionalEmail.isEmpty()) {
            return new ResponseDTO("error", "Введен неверный или истекший код для сброса пароля.");
        }

        String email = optionalEmail.get();
        Client client = clientRepository.findByEmail(email).orElseThrow(OtpValidationException::new);
        if (passwordEncoder.matches(newPassword, client.getPassword())) {
            return new ResponseDTO("passwordMatch", "Вы ввели действующий пароль.");
        }
        client.setPassword(passwordEncoder.encode(newPassword));
        clientRepository.save(client);
        return new ResponseDTO("success", "Забытый пароль успешно изменен.");
    }


    // еще проверка времени типа если время отправки запроса больше чем exp time тогда мы запрещаем
    // и удаляем из БД otp этот код т.к. он уже истек.


}








