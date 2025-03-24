package com.parkrenthub.parking_rent_hub.controllers;

import com.parkrenthub.parking_rent_hub.dto.AddBalanceDTO;
import com.parkrenthub.parking_rent_hub.dto.ResponseDTO;
import com.parkrenthub.parking_rent_hub.security.ClientDetails;
import com.parkrenthub.parking_rent_hub.services.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/add-balance")
    public ResponseEntity<ResponseDTO> addBalance(@AuthenticationPrincipal ClientDetails clientDetails,
                                                  @RequestBody @Valid AddBalanceDTO addBalanceDTO,
                                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new ResponseDTO("error", error));
        }
        String username = clientDetails.getUsername();
        ResponseDTO response = paymentService.addBalance(username, addBalanceDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }
}
