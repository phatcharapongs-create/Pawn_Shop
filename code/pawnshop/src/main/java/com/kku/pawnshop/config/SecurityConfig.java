package com.kku.pawnshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/register", "/css/**", "/js/**", "/images/**", "/error").permitAll()
                .requestMatchers("/account/**").hasAnyRole("ADMIN", "CUSTOMER")
                .requestMatchers("/home").hasAnyRole("ADMIN", "CUSTOMER")
                .requestMatchers("/customer", "/customer/**").hasRole("CUSTOMER")
                .requestMatchers("/admin", "/admin/**", "/customers/**", "/gold-prices/**", "/forfeited-items/**",
                        "/reports/**", "/pawn-tickets/**", "/appraisals/**",
                        "/api/**", "/swagger-ui/**", "/v3/api-docs/**").hasRole("ADMIN")
                .requestMatchers("/").permitAll()
                .anyRequest().authenticated())
            .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/home", true).permitAll())
            .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll())
            .exceptionHandling(errors -> errors.accessDeniedPage("/access-denied"));
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
