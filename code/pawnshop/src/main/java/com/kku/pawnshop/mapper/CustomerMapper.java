package com.kku.pawnshop.mapper;

import com.kku.pawnshop.dto.request.CustomerRequest;
import com.kku.pawnshop.dto.response.CustomerResponse;
import com.kku.pawnshop.model.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerResponse toResponse(Customer customer) {
        if (customer == null) {
            return null;
        }
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setName(customer.getName());
        response.setCitizenId(customer.getCitizenId());
        response.setPhoneNumber(customer.getPhoneNumber());
        response.setAddress(customer.getAddress());
        response.setCreatedAt(customer.getCreatedAt());
        response.setUpdatedAt(customer.getUpdatedAt());
        return response;
    }

    public Customer toEntity(CustomerRequest request) {
        if (request == null) {
            return null;
        }
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setCitizenId(request.getCitizenId());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setAddress(request.getAddress());
        return customer;
    }

    public void updateEntityFromRequest(CustomerRequest request, Customer customer) {
        if (request == null || customer == null) {
            return;
        }
        customer.setName(request.getName());
        customer.setCitizenId(request.getCitizenId());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setAddress(request.getAddress());
    }
}
