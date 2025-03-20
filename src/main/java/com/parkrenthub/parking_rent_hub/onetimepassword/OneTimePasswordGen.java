package com.parkrenthub.parking_rent_hub.onetimepassword;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;

@Component
public class OneTimePasswordGen {
    static SecureRandom random = new SecureRandom();


    public Integer generateOtp() {
        return random.nextInt(100000, 999999);
    }
}
