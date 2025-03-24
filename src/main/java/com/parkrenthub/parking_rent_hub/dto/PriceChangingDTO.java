package com.parkrenthub.parking_rent_hub.dto;

import lombok.Data;

@Data
public class PriceChangingDTO {
    private Integer newDailyPrice;
    private Integer newMonthlyPrice;
}
