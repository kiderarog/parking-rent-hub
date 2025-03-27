package com.parkrenthub.parking_rent_hub.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Penalty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "client_id")
    private UUID clientId;

    @Column(name = "penalty_date")
    private LocalDateTime penaltyDate;

    @Column(name = "penalty_sum")
    private Integer penaltySum;


}
