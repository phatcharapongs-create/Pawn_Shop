package com.kku.pawnshop.service;

import com.kku.pawnshop.domain.entity.UserAccount;
import com.kku.pawnshop.repository.UserAccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AccountUserDetailsService implements UserDetailsService {
    private final UserAccountRepository accountRepository;

    public AccountUserDetailsService(UserAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String normalized = username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
        UserAccount account = accountRepository.findByUsername(normalized)
                .orElseThrow(() -> new UsernameNotFoundException("ไม่พบบัญชีผู้ใช้"));
        return User.withUsername(account.getUsername())
                .password(account.getPasswordHash())
                .roles(account.getRole().name())
                .disabled(!account.isEnabled())
                .build();
    }
}
