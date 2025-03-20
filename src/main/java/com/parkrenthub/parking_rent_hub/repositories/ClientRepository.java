package com.parkrenthub.parking_rent_hub.repositories;

import com.parkrenthub.parking_rent_hub.models.Client;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends MongoRepository<Client, UUID> {

     Optional<Client> findByUsername(String username);

    Optional<Client> findByEmail(@NotEmpty @Email(message = "Указан некорректный формат адреса электронной почты.") String email);
}
