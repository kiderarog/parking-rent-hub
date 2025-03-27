package com.parkrenthub.parking_rent_hub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingStatsDTO {
    private String name;
    private String location;
    private Integer totalSpots;
    private Integer availableSpots;
    private Integer bookedSpots;
    private Integer dailyPrice;
    private Integer monthlyPrice;
    private Boolean isFreeze;
}
