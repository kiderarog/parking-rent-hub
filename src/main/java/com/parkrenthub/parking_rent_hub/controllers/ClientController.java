//package com.parkrenthub.parking_rent_hub.controllers;
//
//import com.parkrenthub.parking_rent_hub.dto.AuthClientDTO;
//import com.parkrenthub.parking_rent_hub.models.Client;
//import com.parkrenthub.parking_rent_hub.services.AuthService;
//import com.parkrenthub.parking_rent_hub.services.ClientService;
//import com.parkrenthub.parking_rent_hub.validation.PasswordChangeGroup;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Map;
//import java.util.Objects;
//
//@RestController
//public class ClientController {
//    private final ClientService clientService;
//
//    public ClientController(ClientService clientService) {
//        this.clientService = clientService;
//    }
//
//    @PostMapping("/change-password")
//    public ResponseEntity<?> changePassword(@RequestBody @Validated(PasswordChangeGroup.class) AuthClientDTO authClientDTO,
//                                            BindingResult bindingResult) {
//        if (bindingResult.hasErrors()) {
//            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
//            return ResponseEntity.badRequest().body(Map.of("error", Objects.requireNonNull(errorMessage)));
//        }
//
//        Map<String, String> response = authService.changePassword(authClientDTO);
//        if (response.containsKey("fail")) {
//            return ResponseEntity.badRequest().body(response);
//        }
//        return ResponseEntity.accepted().body(response);
//    }
//}
