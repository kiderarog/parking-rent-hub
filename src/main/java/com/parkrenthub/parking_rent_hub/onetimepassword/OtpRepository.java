package com.parkrenthub.parking_rent_hub.onetimepassword;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpEntity, String> {

    Optional<OtpEntity> findByOtpCode(Integer otpCode);

    Optional<OtpEntity> findByEmail(String email);

    void deleteAll();

}
