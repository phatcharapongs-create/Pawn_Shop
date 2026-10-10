package com.kku.pawnshop.controller.web;

import com.kku.pawnshop.service.ForfeitedItemService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CustomerProductWebController {
    private final ForfeitedItemService forfeitedItemService;

    public CustomerProductWebController(ForfeitedItemService forfeitedItemService) {
        this.forfeitedItemService = forfeitedItemService;
    }

    @GetMapping("/customer/products")
    public String availableProducts(@RequestParam(defaultValue = "0") int page, Model model) {
        var products = forfeitedItemService.listAvailableProducts(
                PageRequest.of(Math.max(0, page), 12, Sort.by(Sort.Direction.DESC, "graceEndDate")));
        model.addAttribute("products", products);
        return "customer/products";
    }
}
