package com.kku.pawnshop.service;

import com.kku.pawnshop.domain.entity.Customer;
import com.kku.pawnshop.domain.entity.UserAccount;
import com.kku.pawnshop.domain.enums.AccountRole;
import com.kku.pawnshop.dto.response.CustomerAccountView;
import com.kku.pawnshop.exception.BusinessRuleViolationException;
import com.kku.pawnshop.exception.ResourceNotFoundException;
import com.kku.pawnshop.repository.CustomerRepository;
import com.kku.pawnshop.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.nio.charset.StandardCharsets;

@Service
public class AccountManagementService {
    private final UserAccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountManagementService(UserAccountRepository accountRepository, CustomerRepository customerRepository,
                                    PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<CustomerAccountView> customerAccounts() {
        return accountRepository.findAllByOrderByUsernameAsc().stream()
                .filter(account -> account.getRole() == AccountRole.CUSTOMER)
                .map(account -> new CustomerAccountView(account.getId(), account.getUsername(),
                        account.getCustomer() == null ? "—" : account.getCustomer().getFirstName() + " " + account.getCustomer().getLastName(),
                        account.isEnabled())).toList();
    }

    @Transactional
    public void createCustomerAccount(Long customerId, String username, String rawPassword) {
        String normalized = username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
        if (normalized.length() < 3 || normalized.length() > 80) {
            throw new BusinessRuleViolationException("ชื่อผู้ใช้ต้องมีความยาว 3–80 ตัวอักษร");
        }
        validatePassword(rawPassword, "รหัสผ่าน");
        if (accountRepository.existsByUsername(normalized)) {
            throw new BusinessRuleViolationException("ชื่อผู้ใช้นี้ถูกใช้แล้ว");
        }
        if (accountRepository.existsByCustomerId(customerId)) {
            throw new BusinessRuleViolationException("ลูกค้ารายนี้มีบัญชีใช้งานแล้ว");
        }
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("ลูกค้า", customerId));
        UserAccount account = new UserAccount();
        account.setUsername(normalized);
        account.setPasswordHash(passwordEncoder.encode(rawPassword));
        account.setRole(AccountRole.CUSTOMER);
        account.setCustomer(customer);
        account.setEnabled(true);
        accountRepository.save(account);
    }

    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword, String confirmation) {
        UserAccount account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบบัญชีผู้ใช้"));
        if (!passwordEncoder.matches(currentPassword == null ? "" : currentPassword, account.getPasswordHash())) {
            throw new BusinessRuleViolationException("รหัสผ่านปัจจุบันไม่ถูกต้อง");
        }
        validatePassword(newPassword, "รหัสผ่านใหม่");
        if (!newPassword.equals(confirmation)) {
            throw new BusinessRuleViolationException("ยืนยันรหัสผ่านใหม่ไม่ตรงกัน");
        }
        account.setPasswordHash(passwordEncoder.encode(newPassword));
    }

    private void validatePassword(String password, String fieldName) {
        if (password == null || password.length() < 12) {
            throw new BusinessRuleViolationException(fieldName + "ต้องมีอย่างน้อย 12 ตัวอักษร");
        }
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new BusinessRuleViolationException(fieldName + "ยาวเกินไป กรุณาใช้รหัสผ่านที่มีขนาดไม่เกิน 72 ไบต์ (แนะนำภาษาอังกฤษและตัวเลข)");
        }
    }
}
