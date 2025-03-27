package com.parkrenthub.parking_rent_hub.onetimepassword;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OtpService {
    private final OtpRepository otpRepository;
    private final OneTimePasswordGen oneTimePasswordGen;

    @Autowired
    public OtpService(OtpRepository otpRepository, OneTimePasswordGen oneTimePasswordGen) {
        this.otpRepository = otpRepository;
        this.oneTimePasswordGen = oneTimePasswordGen;
    }


    @Transactional
    public void addOtp(String email) {
        OtpEntity otp = new OtpEntity();
        otp.setEmail(email);
        otp.setOtpCode(oneTimePasswordGen.generateOtp());
        otp.setIssuedAt(LocalDateTime.now());
        otp.setExpAt(LocalDateTime.now().plusMinutes(1));
        otpRepository.save(otp);
    }

    // Просто достаем email пользователя из БД по OTP коду, который выслали ему на почту.
    @Transactional
    public Optional<String> findClientEmailByOtp(Integer otp) {
        Optional<OtpEntity> optionalOtpEntity = otpRepository.findByOtpCode(otp);
        if (optionalOtpEntity.isPresent()) {
            OtpEntity otpEntity = optionalOtpEntity.get();
            if (otpEntity.getExpAt().isBefore(LocalDateTime.now())) {
                otpRepository.delete(otpEntity);
                return Optional.empty();
            }
            return Optional.of(otpEntity.getEmail());
        }
        return Optional.empty();
    }


    @Transactional
    public Integer findOtpCodeByEmail(String email) {
        Optional<Integer> optionalCode = otpRepository.findByEmail(email).map(OtpEntity::getOtpCode);
        return optionalCode.orElse(null);
    }




}

