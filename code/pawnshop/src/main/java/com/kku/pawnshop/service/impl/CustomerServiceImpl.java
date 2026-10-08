package com.kku.pawnshop.service.impl;

import com.kku.pawnshop.dto.request.CustomerRequest;
import com.kku.pawnshop.dto.response.CustomerResponse;
import com.kku.pawnshop.exception.DuplicateResourceException;
import com.kku.pawnshop.mapper.CustomerMapper;
import com.kku.pawnshop.model.Customer;
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

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final PawnTicketRepository pawnTicketRepository;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponse> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable)
                .map(customerMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ไม่พบข้อมูลลูกค้า ID: " + id));
        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        if (customerRepository.existsByCitizenId(request.getCitizenId())) {
            throw new DuplicateResourceException("เลขประจำตัวประชาชนนี้มีอยู่ในระบบแล้ว: " + request.getCitizenId());
        }
        Customer customer = customerMapper.toEntity(request);
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    @Transactional
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ไม่พบข้อมูลลูกค้า ID: " + id));

        if (!customer.getCitizenId().equals(request.getCitizenId()) &&
                customerRepository.existsByCitizenId(request.getCitizenId())) {
            throw new DuplicateResourceException("เลขประจำตัวประชาชนนี้ถูกใช้งานโดยลูกค้ารายอื่นแล้ว");
        }

        customerMapper.updateEntityFromRequest(request, customer);
        Customer updatedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(updatedCustomer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ไม่พบข้อมูลลูกค้า ID: " + id));

        // ตรวจสอบเงื่อนไขตามข้อกำหนด: หากมีตั๋วจำนำผูกอยู่ ห้ามลบและตอบ 409 Conflict
        boolean hasActiveTickets = pawnTicketRepository.existsByCustomerId(id);
        if (hasActiveTickets) {
            throw new DuplicateResourceException("ไม่สามารถลบข้อมูลลูกค้าได้ เนื่องจากมีประวัติตั๋วจำนำในระบบ (409 Conflict)");
        }

        customerRepository.delete(customer);
    }
}
