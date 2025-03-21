package com.parkrenthub.parking_rent_hub.controllers;

import com.parkrenthub.parking_rent_hub.dto.AuthClientDTO;
import com.parkrenthub.parking_rent_hub.dto.ClientDTO;
import com.parkrenthub.parking_rent_hub.dto.EditProfileDTO;
import com.parkrenthub.parking_rent_hub.dto.ResponseDTO;
import com.parkrenthub.parking_rent_hub.security.ClientDetails;
import com.parkrenthub.parking_rent_hub.services.ClientService;
import com.parkrenthub.parking_rent_hub.validation.PasswordChangeGroup;
import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Все ручки в ClientController требуют аутентификации и авторизации через JWT-токен.
@RestController
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    // Вывод информации в профиль пользователя.
    // Пользователь выгружается из БД на основании username, извлекаемого из JWT-токена при отправке запроса.
    @GetMapping("/profile")
    public ResponseEntity<ClientDTO> showProfile(@AuthenticationPrincipal ClientDetails clientDetails) {
        return ResponseEntity.ok(clientService.showProfile(clientDetails.getUsername()));
    }

    // Изменение пользовательских данных в профиле.
    @PostMapping("/edit-profile")
    public ResponseEntity<ResponseDTO> editProfile(@AuthenticationPrincipal ClientDetails clientDetails, @RequestBody
                                                   @Valid EditProfileDTO editProfileDTO,
                                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(new ResponseDTO("error", errors));
        }
        String clientName = clientDetails.getUsername();
        ResponseDTO response = clientService.editProfile(clientName, editProfileDTO);
        if ("error".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Удаление аккаунта пользователя из приложения.
    // Процедура удаления доступна только при отсутствии активных бронирований у пользователя.
    @PostMapping("/delete-client")
    public ResponseEntity<ResponseDTO> deleteClient(@AuthenticationPrincipal ClientDetails clientDetails) {
        return ResponseEntity.ok(clientService.deleteProfile(clientDetails.getUsername()));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ResponseDTO> changePassword(@RequestBody @Validated(PasswordChangeGroup.class) AuthClientDTO authClientDTO,
                                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new ResponseDTO("error", error));
        }
        ResponseDTO response = clientService.changePassword(authClientDTO);
        if ("error".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

