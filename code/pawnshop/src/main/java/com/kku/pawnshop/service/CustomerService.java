package com.kku.pawnshop.service;

import com.kku.pawnshop.domain.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** เจ้าของ: สมาชิก A */
public interface CustomerService {

    Customer create(Customer customer);

    Customer update(Long id, Customer customer);

    Customer findById(Long id);

    Page<Customer> findAll(Pageable pageable);

    void delete(Long id);

    /** ตรวจว่าลูกค้ามีสิทธิ์จำนำหรือไม่ เช่น ไม่อยู่ในบัญชีระงับสิทธิ์ */
    void assertEligibleToPawn(Long customerId);
}
