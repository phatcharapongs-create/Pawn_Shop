package com.kku.pawnshop.controller;

import com.kku.pawnshop.controller.api.CustomerRestController;
import com.kku.pawnshop.dto.response.CustomerResponse;
import com.kku.pawnshop.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerRestController.class)
class CustomerRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Test
    void getCustomerById_WhenExists_ShouldReturnCustomer() throws Exception {
        CustomerResponse customerResponse = new CustomerResponse(
                1L, "สมชาย ใจดี", "1234567890123", "0812345678", "ขอนแก่น", LocalDateTime.now(), LocalDateTime.now()
        );

        given(customerService.getCustomerById(1L)).willReturn(customerResponse);

        mockMvc.perform(get("/api/v1/customers/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("สมชาย ใจดี"))
                .andExpect(jsonPath("$.citizenId").value("1234567890123"));
    }
}
