package com.parkrenthub.parking_rent_hub.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class CreateParkingDTO {

    @NotEmpty
    private String name;

    @NotEmpty
    private String location;

    private Integer totalSpots;

    private Integer availableSpots;

    @NotNull
    private Integer dailyPrice;

    @NotNull
    private Integer monthlyPrice;

}
