package com.parkrenthub.parking_rent_hub.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PriceChangingDTO {
    private Integer newDailyPrice;
    private Integer newMonthlyPrice;
}
