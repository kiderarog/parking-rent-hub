package com.parkrenthub.parking_rent_hub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParkingInfoDTO {
    private String name;
    private String location;
    private Integer totalSpots;
    private Integer availableSpots;
    private Integer dailyPrice;
    private Integer monthlyPrice;
}
