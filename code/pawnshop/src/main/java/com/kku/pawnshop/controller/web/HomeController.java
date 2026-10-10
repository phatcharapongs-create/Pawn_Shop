package com.kku.pawnshop.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** หน้าเริ่มต้นและทางแยกหลังเข้าสู่ระบบ */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/home")
    public String afterLogin(Authentication authentication) {
        if (authentication == null) return "redirect:/login";
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).anyMatch("ROLE_ADMIN"::equals);
        return isAdmin ? "redirect:/admin" : "redirect:/customer";
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout,
                        org.springframework.ui.Model model) {
        if (error != null) model.addAttribute("loginError", true);
        if (logout != null) model.addAttribute("loggedOut", true);
        return "auth/login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "auth/access-denied";
    }
}
