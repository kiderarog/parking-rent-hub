//package com.parkrenthub.parking_rent_hub.services;
//
//import org.springframework.stereotype.Service;
//
//@Service
//public class ClientService {
//
//
//    // МЕТОД ДЛЯ CLIENT SERVICE
////    @Transactional
////    public Map<String, String> changePassword(AuthClientDTO authClientDTO) {
////        Client client = clientRepository.findByUsername(authClientDTO.getUsername())
////                .orElseThrow(() -> new RuntimeException("Пользователь не найден."));
////
////        if (passwordEncoder.matches(authClientDTO.getPassword(), client.getPassword())) {
////            client.setPassword(passwordEncoder.encode(authClientDTO.getNewPassword()));
////            clientRepository.save(client);
////            return Map.of("success", "Ваш пароль был успешно изменен.");
////        } else {
////            return Map.of("fail", "Неверный текущий пароль.");
////        }
////    }
//}
