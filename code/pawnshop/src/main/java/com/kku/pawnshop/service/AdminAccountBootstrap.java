package com.kku.pawnshop.service;

import com.kku.pawnshop.domain.entity.UserAccount;
import com.kku.pawnshop.domain.enums.AccountRole;
import com.kku.pawnshop.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class AdminAccountBootstrap implements ApplicationRunner {
    private final UserAccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final String username;
    private final String password;

    public AdminAccountBootstrap(UserAccountRepository accountRepository, PasswordEncoder passwordEncoder,
                                 @Value("${PAWNSHOP_ADMIN_USERNAME:}") String username,
                                 @Value("${PAWNSHOP_ADMIN_PASSWORD:}") String password) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.username = username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
        this.password = password == null ? "" : password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (username.isBlank() && password.isBlank()) return;
        if (username.isBlank() || password.length() < 12) {
            throw new IllegalStateException("กำหนด PAWNSHOP_ADMIN_USERNAME และ PAWNSHOP_ADMIN_PASSWORD อย่างน้อย 12 ตัวอักษรให้ครบ");
        }
        accountRepository.findByUsername(username).ifPresentOrElse(existing -> {
            if (existing.getRole() != AccountRole.ADMIN) {
                throw new IllegalStateException("ชื่อบัญชี bootstrap นี้ถูกใช้โดยบัญชีที่ไม่ใช่ ADMIN แล้ว");
            }
        }, () -> {
            UserAccount account = new UserAccount();
            account.setUsername(username);
            account.setPasswordHash(passwordEncoder.encode(password));
            account.setRole(AccountRole.ADMIN);
            account.setEnabled(true);
            accountRepository.save(account);
        });
    }
}
