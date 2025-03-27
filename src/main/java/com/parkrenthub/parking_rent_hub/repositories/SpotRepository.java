package com.parkrenthub.parking_rent_hub.repositories;

import com.parkrenthub.parking_rent_hub.models.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpotRepository extends JpaRepository<Spot, UUID> {
    boolean existsByParkingIdAndActiveBookingIsTrue(UUID parkingId);

    Optional<Spot> findFirstByParkingIdAndActiveBookingIsFalse(UUID parkingId);

    Optional<Spot> findByClientId(UUID clientId);


}


