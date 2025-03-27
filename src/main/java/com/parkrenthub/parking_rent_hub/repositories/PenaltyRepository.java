package com.parkrenthub.parking_rent_hub.repositories;

import com.parkrenthub.parking_rent_hub.models.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;


@Repository
public interface PenaltyRepository extends JpaRepository<Penalty, Integer> {


}
