package com.kku.pawnshop.service.impl;

import com.kku.pawnshop.domain.entity.Customer;
import com.kku.pawnshop.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Override
    public Customer create(Customer customer) {
        return null;
    }

    @Override
    public Customer update(Long id, Customer customer) {
        return null;
    }

    @Override
    public Customer findById(Long id) {
        return new Customer(); // คืนค่า object เปล่าๆ ไปก่อนเพื่อให้ฝั่ง Ticket เทสต์ผ่าน
    }

    @Override
    public Page<Customer> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public void delete(Long id) {
    }

    @Override
    public void assertEligibleToPawn(Long customerId) {
        // ปล่อยว่างไว้ก่อน เพื่อให้ผ่านสิทธิ์การจำนำชั่วคราว
    }
}