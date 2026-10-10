package com.kku.pawnshop.controller.web;

import com.kku.pawnshop.dto.response.CustomerResponse;
import com.kku.pawnshop.service.AccountManagementService;
import com.kku.pawnshop.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminAccountWebController {
    private final AccountManagementService accountService;
    private final CustomerService customerService;

    public AdminAccountWebController(AccountManagementService accountService, CustomerService customerService) {
        this.accountService = accountService;
        this.customerService = customerService;
    }

    @GetMapping("/admin/accounts")
    public String accounts(Model model) {
        model.addAttribute("accounts", accountService.customerAccounts());
        model.addAttribute("customers", customerService.findAll());
        return "admin/accounts";
    }

    @PostMapping("/admin/accounts")
    public String createAccount(@RequestParam Long customerId, @RequestParam String username,
                                @RequestParam String password, RedirectAttributes redirect) {
        try {
            accountService.createCustomerAccount(customerId, username, password);
            redirect.addFlashAttribute("successMessage", "สร้างบัญชีลูกค้าเรียบร้อย");
        } catch (RuntimeException ex) {
            redirect.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/accounts";
    }
}
