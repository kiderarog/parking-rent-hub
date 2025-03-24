package com.parkrenthub.parking_rent_hub.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final ClientDetailsService clientDetailsService;
    private final JWTFilter jwtFilter;

    @Autowired
    public SecurityConfig(ClientDetailsService clientDetailsService, JWTFilter jwtFilter) {
        this.clientDetailsService = clientDetailsService;
        this.jwtFilter = jwtFilter;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        provider.setUserDetailsService(clientDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)  // Отключаем CSRF
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/auth/**", "/auth/reset-password").permitAll()
                        .requestMatchers("/change-password").authenticated()  // Требуем авторизацию для /protected-endpoint
                        .requestMatchers(("/admin/**")).hasRole("ADMIN")
                        .anyRequest().authenticated())  // Для всех остальных запросов — требовать авторизацию
                .httpBasic(Customizer.withDefaults())  // Стандартная авторизация HTTP
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Без сессий
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)  // Добавляем фильтр для JWT
                .build();
    }
}
