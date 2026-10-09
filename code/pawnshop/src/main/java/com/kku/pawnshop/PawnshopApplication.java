package com.kku.pawnshop;

import com.kku.pawnshop.domain.vo.Money;
import com.kku.pawnshop.service.CustomerService;
import com.kku.pawnshop.service.LedgerService;
import com.kku.pawnshop.service.interest.InterestCalculator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
public class PawnshopApplication {

    public static void main(String[] args) {
        SpringApplication.run(PawnshopApplication.class, args);
    }

    @Bean
    public InterestCalculator interestCalculator() {
        return (ticket, asOf) -> Money.of(BigDecimal.ZERO);
    }

    @Bean
    public LedgerService ledgerService() {
        return new LedgerService() {};
    }

    @Bean
    public CustomerService customerService() {
        return new CustomerService() {};
    }
}