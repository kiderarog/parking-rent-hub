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
    private final PyrusService pyrusService;

    public AuthService(ClientRepository clientRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper, JWTUtil jwtUtil, EmailSender emailSender, OtpService otpService, PyrusService pyrusService) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.jwtUtil = jwtUtil;
        this.emailSender = emailSender;
        this.otpService = otpService;
        this.pyrusService = pyrusService;
    }


    // Метод для сохранения пользователя при регистрации.
    // Пользователь сохраняется в БД, а также передается в CRM с помощью методов из PyrusService.
    @Transactional
    public void saveClient(AuthClientDTO authClientDTO) {
        Client client = modelMapper.map(authClientDTO, Client.class);
        client.setPassword(passwordEncoder.encode(client.getPassword()));
        client.setRole(Roles.ROLE_USER);
        client.setBalance(0.0);
        client.setTotalPenaltySum(0);
        clientRepository.save(client);
        pyrusService.addClientCRM(client);
    }

    // Метод для получения JWT-токена для авторизации пользователя.
    @Transactional
    public ResponseDTO getAuthorizationToken(AuthClientDTO authClientDTO) {
        Optional<Client> optionalClient =
                clientRepository.findByUsername(authClientDTO.getUsername());
        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();
            if (passwordEncoder.matches(authClientDTO.getPassword(), client.getPassword())) {
                return new ResponseDTO("token", jwtUtil.generateToken(client.getUsername(), client.getRole().name(), client.getId()));
            }
        }
        return new ResponseDTO("error", "Неверные имя пользователя или пароль.");
    }

    // Метод для инициализации процесса сброса пароля через одноразовый код,
    // Который высылается на почту пользователю и имеет срок действия в 5 минут.
    @Transactional
    public ResponseDTO resetPasswordRequestProcessing(String email) {
        Optional<Client> optionalClient =
                clientRepository.findByEmail(email);
        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();
            otpService.addOtp(email);
            emailSender.sendOtp(client);
            return new ResponseDTO("success", "Код восстановления отправлен на Ваш Email-адрес.");
        }
        return new ResponseDTO("error", "Пользователь с таким Email не найден");
    }

    // Метод для проверки введенного одноразового пароля и смены пароля в случае успешного ввода OTP.
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


}








