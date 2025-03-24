package com.parkrenthub.parking_rent_hub.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AddBalanceDTO {

    @NotEmpty
    @Pattern(regexp = "\\d{16}", message = "Номер карты должен содержать ровно 16 цифр.")
    private String cardNumber;

    @NotNull(message = "Невозможно отправить пустое значение.")
    @Min(value = 100, message = "Минимальная сумма пополнения: 100.")
    private Double money;
}
