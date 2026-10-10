package com.kku.pawnshop.controller.web;

import com.kku.pawnshop.service.AccountManagementService;
import com.kku.pawnshop.service.CustomerPortalService;
import com.kku.pawnshop.exception.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CustomerPortalWebController {
    private final CustomerPortalService portalService;
    private final AccountManagementService accountService;

    public CustomerPortalWebController(CustomerPortalService portalService, AccountManagementService accountService) {
        this.portalService = portalService;
        this.accountService = accountService;
    }

    @GetMapping("/customer")
    public String dashboard(@RequestParam(defaultValue = "0") int page, Authentication authentication, Model model) {
        try {
            var snapshot = portalService.dashboard(authentication.getName(), page);
            model.addAttribute("customerName", snapshot.customerName());
            model.addAttribute("tickets", snapshot.tickets());
            return "customer/dashboard";
        } catch (ResourceNotFoundException ex) {
            // Session เก่าอาจค้างหลังสลับฐานข้อมูลหรือเปลี่ยนชุดไฟล์ ให้กลับไปยืนยันบัญชีใหม่
            return "redirect:/login?accountMissing=true";
        }
    }

    @GetMapping("/account/password")
    public String passwordForm() {
        return "customer/password";
    }

    @PostMapping("/account/password")
    public String changePassword(Authentication authentication, @RequestParam String currentPassword,
                                 @RequestParam String newPassword, @RequestParam String confirmPassword,
                                 RedirectAttributes redirect) {
        try {
            accountService.changePassword(authentication.getName(), currentPassword, newPassword, confirmPassword);
            redirect.addFlashAttribute("successMessage", "เปลี่ยนรหัสผ่านเรียบร้อย");
        } catch (RuntimeException ex) {
            redirect.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/account/password";
    }
}
