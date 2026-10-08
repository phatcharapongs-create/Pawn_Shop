package com.kku.pawnshop.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kku.pawnshop.dto.request.CustomerRequest;
import com.kku.pawnshop.dto.response.CustomerResponse;
import com.kku.pawnshop.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerRestController.class)
class CustomerRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    // 1. GET - รายการลูกค้าทั้งหมด (Pagination)
    @Test
    void getAllCustomers_ShouldReturnPageOfCustomers() throws Exception {
        CustomerResponse customerResponse = new CustomerResponse(
                1L, "สมชาย ใจดี", "1234567890123", "0812345678", "ขอนแก่น", LocalDateTime.now(), LocalDateTime.now()
        );
        PageImpl<CustomerResponse> page = new PageImpl<>(List.of(customerResponse), PageRequest.of(0, 10), 1);

        given(customerService.getAllCustomers(any())).willReturn(page);

        mockMvc.perform(get("/api/v1/customers")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("สมชาย ใจดี"));
    }

    // 2. GET - ดึงข้อมูลตาม ID
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

    // 3. POST - สร้างลูกค้าสำเร็จ (201 Created)
    @Test
    void createCustomer_WhenValidRequest_ShouldReturnCreated() throws Exception {
        CustomerRequest request = new CustomerRequest("สมชาย ใจดี", "1234567890123", "0812345678", "ขอนแก่น");
        CustomerResponse response = new CustomerResponse(1L, "สมชาย ใจดี", "1234567890123", "0812345678", "ขอนแก่น", LocalDateTime.now(), LocalDateTime.now());

        given(customerService.createCustomer(any(CustomerRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("สมชาย ใจดี"));
    }

    // 4. POST - ข้อมูลไม่ถูกต้อง (Validation Error - 400 Bad Request)
    @Test
    void createCustomer_WhenInvalidCitizenId_ShouldReturnBadRequest() throws Exception {
        // เลขบัตรไม่ครบ 13 หลัก
        CustomerRequest request = new CustomerRequest("สมชาย ใจดี", "1234", "0812345678", "ขอนแก่น");

        mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // 5. PUT - แก้ไขข้อมูลลูกค้า (200 OK)
    @Test
    void updateCustomer_WhenValid_ShouldReturnUpdated() throws Exception {
        CustomerRequest request = new CustomerRequest("สมชาย ใจดีมาก", "1234567890123", "0812345678", "ขอนแก่น");
        CustomerResponse response = new CustomerResponse(1L, "สมชาย ใจดีมาก", "1234567890123", "0812345678", "ขอนแก่น", LocalDateTime.now(), LocalDateTime.now());

        given(customerService.updateCustomer(eq(1L), any(CustomerRequest.class))).willReturn(response);

        mockMvc.perform(put("/api/v1/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("สมชาย ใจดีมาก"));
    }

    // 6. DELETE - ลบข้อมูลลูกค้า (204 No Content)
    @Test
    void deleteCustomer_WhenSuccess_ShouldReturnNoContent() throws Exception {
        doNothing().when(customerService).deleteCustomer(1L);

        mockMvc.perform(delete("/api/v1/customers/1"))
                .andExpect(status().isNoContent());
    }
}
