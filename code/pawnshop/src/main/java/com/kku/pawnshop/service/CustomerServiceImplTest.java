package com.kku.pawnshop.service;

import com.kku.pawnshop.dto.request.CustomerRequest;
import com.kku.pawnshop.dto.response.CustomerResponse;
import com.kku.pawnshop.exception.DuplicateResourceException;
import com.kku.pawnshop.mapper.CustomerMapper;
import com.kku.pawnshop.model.Customer;
import com.kku.pawnshop.repository.CustomerRepository;
import com.kku.pawnshop.repository.PawnTicketRepository;
import com.kku.pawnshop.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PawnTicketRepository pawnTicketRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private CustomerRequest customerRequest;
    private CustomerResponse customerResponse;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("สมชาย ใจดี");
        customer.setCitizenId("1234567890123");
        customer.setPhoneNumber("0812345678");

        customerRequest = new CustomerRequest("สมชาย ใจดี", "1234567890123", "0812345678", "ขอนแก่น");
        customerResponse = new CustomerResponse(1L, "สมชาย ใจดี", "1234567890123", "0812345678", "ขอนแก่น", null, null);
    }

    @Test
    void createCustomer_Success() {
        when(customerRepository.existsByCitizenId(anyString())).thenReturn(false);
        when(customerMapper.toEntity(any(CustomerRequest.class))).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(customerMapper.toResponse(any(Customer.class))).thenReturn(customerResponse);

        CustomerResponse result = customerService.createCustomer(customerRequest);

        assertNotNull(result);
        assertEquals("สมชาย ใจดี", result.getName());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void deleteCustomer_WhenHasActiveTickets_ThrowsDuplicateResourceException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(pawnTicketRepository.existsByCustomerId(1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> customerService.deleteCustomer(1L));
        verify(customerRepository, never()).delete(any(Customer.class));
    }
}
