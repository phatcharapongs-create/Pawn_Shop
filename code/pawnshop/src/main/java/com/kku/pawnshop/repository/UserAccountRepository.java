package com.kku.pawnshop.repository;

import com.kku.pawnshop.domain.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);
    Optional<UserAccount> findByCustomerId(Long customerId);
    boolean existsByUsername(String username);
    boolean existsByCustomerId(Long customerId);
    List<UserAccount> findAllByOrderByUsernameAsc();
}
