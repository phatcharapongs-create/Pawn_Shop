package com.kku.pawnshop.controller.web;

import com.kku.pawnshop.dto.request.CustomerRequest;
import com.kku.pawnshop.dto.response.CustomerResponse;
import com.kku.pawnshop.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerWebController {

    private final CustomerService customerService;

    @GetMapping
    public String listCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<CustomerResponse> customerPage = customerService.getAllCustomers(PageRequest.of(page, size));
        model.addAttribute("customers", customerPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", customerPage.getTotalPages());
        return "customers/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("customerRequest", new CustomerRequest());
        return "customers/form";
    }

    @PostMapping
    public String createCustomer(
            @Valid @ModelAttribute("customerRequest") CustomerRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "customers/form";
        }
        customerService.createCustomer(request);
        redirectAttributes.addFlashAttribute("successMessage", "บันทึกข้อมูลลูกค้าสำเร็จ");
        return "redirect:/customers";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        CustomerResponse customer = customerService.getCustomerById(id);
        CustomerRequest request = new CustomerRequest(
                customer.getName(),
                customer.getCitizenId(),
                customer.getPhoneNumber(),
                customer.getAddress()
        );
        model.addAttribute("customerRequest", request);
        model.addAttribute("customerId", id);
        return "customers/form";
    }

    @PostMapping("/{id}")
    public String updateCustomer(
            @PathVariable Long id,
            @Valid @ModelAttribute("customerRequest") CustomerRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("customerId", id);
            return "customers/form";
        }
        customerService.updateCustomer(id, request);
        redirectAttributes.addFlashAttribute("successMessage", "แก้ไขข้อมูลลูกค้าสำเร็จ");
        return "redirect:/customers";
    }

    @PostMapping("/{id}/delete")
    public String deleteCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            customerService.deleteCustomer(id);
            redirectAttributes.addFlashAttribute("successMessage", "ลบข้อมูลลูกค้าสำเร็จ");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/customers";
    }
}