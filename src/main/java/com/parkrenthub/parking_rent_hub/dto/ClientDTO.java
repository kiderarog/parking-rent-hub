package com.parkrenthub.parking_rent_hub.dto;

import com.parkrenthub.parking_rent_hub.validation.PasswordChangeGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {

    @NotEmpty(message = "Имя не должно быть пустым.", groups = {Default.class, PasswordChangeGroup.class})
    @Pattern(regexp = "^[a-zA-Zа-яА-Я0-9]+$", message = "Использованы запрещенные спецсимволы.")
    @Indexed(unique = true)
    private String username;

    @NotEmpty
    private String name;

    @NotEmpty
    private String surname;

    @NotEmpty
    @Email(message = "Указан некорректный формат адреса электронной почты.")
    @Indexed(unique = true)
    private String email;

    @NotEmpty
    @Pattern(regexp = "^\\+7\\d{10}$", message = "Введите номер телефона в формате +7XXXXXXXXXX")
    @Indexed(unique = true)
    private String phone;

    @NotEmpty(message = "Укажите госномер авто.")
    @Pattern(regexp = "^[A-Z]\\d{3}[A-Z]{2}\\d{3}$|^[A-Z]\\d{3}[A-Z]{2}\\d{2}$\n",
            message = "Введите номер автомобиле в формате X000XX000 или X000XX00")
    private String carNumber;

    private Double balance;

    private UUID bookedSpotId;

}
