package com.kku.pawnshop.config;

import com.kku.pawnshop.domain.entity.InterestPolicy;
import com.kku.pawnshop.domain.entity.RateTier;
import com.kku.pawnshop.domain.vo.Money;
import com.kku.pawnshop.repository.InterestPolicyRepository;
import com.kku.pawnshop.repository.RateTierRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Provides sample policy data for the in-memory H2 profile, where Flyway is disabled. */
@Configuration
@Profile("local")
public class LocalInterestPolicyBootstrap {

    @Bean
    ApplicationRunner localInterestPolicyInitializer(InterestPolicyRepository policyRepository,
                                                     RateTierRepository rateTierRepository,
                                                     PlatformTransactionManager transactionManager) {
        TransactionTemplate transaction = new TransactionTemplate(transactionManager);
        return args -> transaction.executeWithoutResult(status -> initializeLocalPolicy(policyRepository, rateTierRepository));
    }

    void initializeLocalPolicy(InterestPolicyRepository policyRepository, RateTierRepository rateTierRepository) {
        LocalDate today = LocalDate.now();
        if (policyRepository.findEffectiveOn(today).isPresent()) {
            return;
        }

        InterestPolicy policy = new InterestPolicy();
        policy.setCode("LOCAL-DEMO-" + today.getYear());
        policy.setName("นโยบายตัวอย่างสำหรับทดสอบระบบ");
        policy.setEffectiveFrom(today);
        policy.setEffectiveTo(null);
        policy.setRedemptionMonths(4);
        policy.setGraceDays(30);
        policy = policyRepository.save(policy);

        RateTier firstTier = new RateTier();
        firstTier.setPolicy(policy);
        firstTier.setLowerBound(Money.of(new BigDecimal("0.00")));
        firstTier.setUpperBound(Money.of(new BigDecimal("2000.00")));
        firstTier.setMonthlyRatePercent(new BigDecimal("2.0000"));
        firstTier.setTierOrder(1);
        rateTierRepository.save(firstTier);

        RateTier nextTier = new RateTier();
        nextTier.setPolicy(policy);
        nextTier.setLowerBound(Money.of(new BigDecimal("2000.01")));
        nextTier.setUpperBound(null);
        nextTier.setMonthlyRatePercent(new BigDecimal("1.2500"));
        nextTier.setTierOrder(2);
        rateTierRepository.save(nextTier);
    }
}
