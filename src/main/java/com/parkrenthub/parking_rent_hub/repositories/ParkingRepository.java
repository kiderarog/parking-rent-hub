package com.parkrenthub.parking_rent_hub.repositories;

import com.parkrenthub.parking_rent_hub.models.Parking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ParkingRepository extends JpaRepository<Parking, UUID> {
}
