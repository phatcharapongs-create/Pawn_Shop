package com.kku.pawnshop.controller.web;

import com.kku.pawnshop.dto.request.CustomerSignupForm;
import com.kku.pawnshop.service.CustomerOnboardingService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CustomerSignupWebController {
    private final CustomerOnboardingService onboardingService;

    public CustomerSignupWebController(CustomerOnboardingService onboardingService) {
        this.onboardingService = onboardingService;
    }

    @GetMapping("/register")
    public String form(Model model) {
        if (!model.containsAttribute("signupForm")) model.addAttribute("signupForm", new CustomerSignupForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("signupForm") CustomerSignupForm form,
                           BindingResult errors, Model model, RedirectAttributes redirect) {
        if (form.getPassword() != null && form.getConfirmPassword() != null
                && !form.getPassword().equals(form.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "mismatch", "รหัสผ่านและการยืนยันรหัสผ่านไม่ตรงกัน");
        }
        if (errors.hasErrors()) return "auth/register";

        try {
            form.setUsername(form.getUsername().trim());
            onboardingService.register(form, true, form.getUsername(), form.getPassword());
            redirect.addAttribute("registered", "true");
            return "redirect:/login";
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "auth/register";
        }
    }
}
