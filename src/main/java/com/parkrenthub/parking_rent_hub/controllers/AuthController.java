package com.parkrenthub.parking_rent_hub.controllers;

import com.parkrenthub.parking_rent_hub.dto.AuthClientDTO;
import com.parkrenthub.parking_rent_hub.dto.OtpEntityDTO;
import com.parkrenthub.parking_rent_hub.dto.ResponseDTO;
import com.parkrenthub.parking_rent_hub.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

// Все ручки в AuthController доступны без аутентификации и авторизации.
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    // Регистрация нового клиента.
    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> createClient(@RequestBody @Valid AuthClientDTO authClientDTO,
                                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(new ResponseDTO("error", errors));
        }
        authService.saveClient(authClientDTO);
        return ResponseEntity.ok(new ResponseDTO("success", "Пользователь успешно зарегистрирован."));
    }


    // Аутентификация клиента и получение токена авторизации (JWT).
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody AuthClientDTO authClientDTO) {
        ResponseDTO response = authService.getAuthorizationToken(authClientDTO);
        if ("error".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Инициализация сброса забытого пароля от аккаунта пользователя.
    // Отправка на Email одноразового пароля для восстановления доступа к аккаунту.
    @PostMapping("/reset-password-request")
    public ResponseEntity<ResponseDTO> resetPasswordRequest(@RequestBody AuthClientDTO authClientDTO) {
        String email = authClientDTO.getEmail();
        ResponseDTO response = authService.resetPasswordRequestProcessing(email);
        if (response.getStatus().equals("success")) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Сброс забытого пароля через форму "Забыли пароль?".
    // Ввод одноразового кода для восстановления, полученного по Email и ввод нового пароля.
    @PostMapping("/reset-password")
    public ResponseEntity<ResponseDTO> resetPassword(@RequestBody @Valid OtpEntityDTO otpEntityDTO,
                                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new ResponseDTO("error", Objects.requireNonNull(errorMessage)));
        }
        Integer otpCode = otpEntityDTO.getOtpCode();
        String newPassword = otpEntityDTO.getNewPassword();
        ResponseDTO response = authService.replaceForgottenPassword(otpCode, newPassword);
        if ("error".equals(response.getStatus()) || "passwordMatch".equals(response.getStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}


