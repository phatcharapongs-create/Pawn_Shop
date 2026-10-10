package com.kku.pawnshop.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Web form that can optionally provision a login account together with a new customer. */
@Getter
@Setter
@NoArgsConstructor
public class CustomerRegistrationForm extends CustomerRequest {
    private boolean createAccount;
    private String username;
    private String initialPassword;
}
