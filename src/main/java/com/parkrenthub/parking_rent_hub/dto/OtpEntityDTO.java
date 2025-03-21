package com.parkrenthub.parking_rent_hub.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class OtpEntityDTO {

    @NotNull
    @Digits(integer = 6, fraction = 0)
    private Integer otpCode;

    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[\\W_]).{8,}$",
            message = "Пароль должен содержать минимум 8 символов, 1 заглавную букву и 1 спецсимвол.")
    private String newPassword;
}
