package com.parkrenthub.parking_rent_hub.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class EditProfileDTO {

    @NotEmpty(message = "Новое имя не должно быть пустым.")
    private String newName;
    @NotEmpty(message = "Новая фамилия не должна быть пустой.")
    @NotEmpty
    private String newSurname;

    @NotEmpty(message = "Укажите госномер авто.")
    @Pattern(regexp = "^[A-Z]\\d{3}[A-Z]{2}\\d{3}$|^[A-Z]\\d{3}[A-Z]{2}\\d{2}$",
            message = "Введите номер автомобиле в формате X000XX000 или X000XX00")
    private String newCarNumber;
}
