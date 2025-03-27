package com.parkrenthub.parking_rent_hub.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "spot")
public class Spot {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID clientId;

    @Column(name = "start_date")
    private LocalDateTime startBookDate;

    @Column(name = "end_date")
    private LocalDateTime endBookDate;

    @Column(name = "car_number")
    private String carNumber;

    @Column(name = "active_booking")
    private Boolean activeBooking;

    @ManyToOne
    @JoinColumn(name = "parking_id")
    @ToString.Exclude
    private Parking parking;

    @Column(name = "car_on_spot_status")
    private Boolean carOnSpotStatus;
}
