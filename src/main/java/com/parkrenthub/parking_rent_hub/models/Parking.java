package com.parkrenthub.parking_rent_hub.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "parking")
@Entity
public class Parking {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String location;

    @Column(name = "total_spots")
    private Integer totalSpots;

    @Column(name = "available_spots")
    private Integer availableSpots;

    @Column(name = "daily_price")
    @NotNull
    private Integer dailyPrice;

    @Column(name = "monthly_price")
    @NotNull
    private Integer monthlyPrice;

    @OneToMany(mappedBy = "parking")
    private List<Spot> spots;

    @Column(name = "is_freeze")
    private boolean isFreeze;

}
