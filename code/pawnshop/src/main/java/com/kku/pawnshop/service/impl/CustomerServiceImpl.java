package com.kku.pawnshop.service.impl;

import com.kku.pawnshop.domain.entity.Customer;
import com.kku.pawnshop.dto.request.CustomerRequest;
import com.kku.pawnshop.dto.response.CustomerResponse;
import com.kku.pawnshop.exception.DuplicateResourceException;
import com.kku.pawnshop.exception.ResourceNotFoundException;
import com.kku.pawnshop.mapper.CustomerMapper;
import com.kku.pawnshop.repository.CustomerRepository;
import com.kku.pawnshop.repository.PawnTicketRepository;
import com.kku.pawnshop.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final PawnTicketRepository pawnTicketRepository;
    private final CustomerMapper customerMapper;

    // === การทำงานกับ DTO (สำหรับ Web Controller / UI) ===

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> findAll() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponse> findAllResponses(Pageable pageable) {
        return customerRepository.findAll(pageable)
                .map(customerMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse findResponseById(Long id) {
        Customer customer = findById(id);
        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        if (customerRepository.existsByCitizenId(request.getCitizenId())) {
            throw new DuplicateResourceException("เลขประจำตัวประชาชนนี้มีอยู่ในระบบแล้ว: " + request.getCitizenId());
        }
        Customer customer = customerMapper.toEntity(request);
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = findById(id);

        if (!customer.getCitizenId().equals(request.getCitizenId()) &&
                customerRepository.existsByCitizenId(request.getCitizenId())) {
            throw new DuplicateResourceException("เลขประจำตัวประชาชนนี้ถูกใช้งานโดยลูกค้ารายอื่นแล้ว");
        }

        customerMapper.updateEntityFromRequest(request, customer);
        Customer updatedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(updatedCustomer);
    }

    // === การทำงานกับ Entity (สำหรับสมาชิกในทีมเรียกใช้) ===

    @Override
    @Transactional
    public Customer create(Customer customer) {
        if (customer.getCitizenId() != null && customerRepository.existsByCitizenId(customer.getCitizenId())) {
            throw new DuplicateResourceException("เลขประจำตัวประชาชนนี้มีอยู่ในระบบแล้ว: " + customer.getCitizenId());
        }
        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    public Customer update(Long id, Customer customer) {
        Customer existing = findById(id);
        if (customer.getCitizenId() != null && !existing.getCitizenId().equals(customer.getCitizenId()) &&
                customerRepository.existsByCitizenId(customer.getCitizenId())) {
            throw new DuplicateResourceException("เลขประจำตัวประชาชนนี้ถูกใช้งานโดยลูกค้ารายอื่นแล้ว");
        }
        customer.setId(id);
        return customerRepository.save(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบข้อมูลลูกค้า ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Customer> findAll(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Customer customer = findById(id);

        // เช็กประวัติตั๋วจำนำ หากมีอยู่จะโยน 409 Conflict
        boolean hasActiveTickets = pawnTicketRepository.existsByCustomerId(id);
        if (hasActiveTickets) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ไม่สามารถลบข้อมูลลูกค้าได้ เนื่องจากมีประวัติตั๋วจำนำอยู่ในระบบ");
        }

        customerRepository.delete(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public void assertEligibleToPawn(Long customerId) {
        Customer customer = findById(customerId);
        if (customer.isBlacklisted()) {
            throw new IllegalStateException("ลูกค้าติดสถานะ Blacklist ไม่สามารถทำรายการได้");
        }
    }
}
