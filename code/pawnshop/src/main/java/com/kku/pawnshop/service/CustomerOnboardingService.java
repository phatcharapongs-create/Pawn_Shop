package com.kku.pawnshop.service;

import com.kku.pawnshop.dto.request.CustomerRequest;
import com.kku.pawnshop.dto.response.CustomerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Creates the customer record and, when requested, its login in one transaction. */
@Service
@RequiredArgsConstructor
public class CustomerOnboardingService {
    private final CustomerService customerService;
    private final AccountManagementService accountManagementService;

    @Transactional
    public void register(CustomerRequest request, boolean createAccount, String username, String initialPassword) {
        CustomerResponse customer = customerService.create(request);
        if (createAccount) {
            accountManagementService.createCustomerAccount(customer.getId(), username, initialPassword);
        }
    }
}
