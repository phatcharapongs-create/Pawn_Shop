package com.kku.pawnshop.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** หน้าแรกของระบบ พาไปหน้ารายการตั๋วจำนำ */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/pawn-tickets";
    }
}
