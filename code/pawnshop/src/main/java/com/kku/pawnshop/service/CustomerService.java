package com.kku.pawnshop.service;

import com.kku.pawnshop.domain.entity.Customer;
import com.kku.pawnshop.dto.request.CustomerRequest;
import com.kku.pawnshop.dto.response.CustomerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {

    // === เมธอดทำงานกับ DTO (สำหรับ Web Controller / UI) ===
    List<CustomerResponse> findAll();

    Page<CustomerResponse> findAllResponses(Pageable pageable);

    CustomerResponse findResponseById(Long id);

    CustomerResponse create(CustomerRequest request);

    CustomerResponse update(Long id, CustomerRequest request);

    // === เมธอดทำงานกับ Entity (สำหรับสมาชิกในทีมเรียกใช้ เช่น openTicket) ===
    Customer create(Customer customer);

    Customer update(Long id, Customer customer);

    Customer findById(Long id);

    Page<Customer> findAll(Pageable pageable);

    void delete(Long id);

    /** ตรวจว่าลูกค้ามีสิทธิ์จำนำหรือไม่ เช่น ไม่อยู่ในบัญชีระงับสิทธิ์ */
    void assertEligibleToPawn(Long customerId);
}
