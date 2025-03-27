package com.parkrenthub.parking_rent_hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ParkingRentHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParkingRentHubApplication.class, args);
	}


}
