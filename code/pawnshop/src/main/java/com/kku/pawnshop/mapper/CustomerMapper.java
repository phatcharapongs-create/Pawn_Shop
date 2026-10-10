package com.kku.pawnshop.mapper;

import com.kku.pawnshop.dto.request.CustomerRequest;
import com.kku.pawnshop.dto.response.CustomerResponse;
import com.kku.pawnshop.domain.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerResponse toResponse(Customer customer) {
        if (customer == null) {
            return null;
        }
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());

        // รวม firstName และ lastName เป็น name
        String firstName = customer.getFirstName() != null ? customer.getFirstName() : "";
        String lastName = customer.getLastName() != null ? customer.getLastName() : "";
        response.setName((firstName + " " + lastName).trim());

        response.setCitizenId(customer.getCitizenId());
        response.setPhoneNumber(customer.getPhone()); // เปลี่ยนเป็น getPhone()
        response.setAddress(customer.getAddress());
        response.setCreatedAt(customer.getCreatedAt());
        // ลบ setUpdatedAt ออกเพราะ Entity Customer ไม่มี field นี้
        
        return response;
    }

    public Customer toEntity(CustomerRequest request) {
        if (request == null) {
            return null;
        }
        Customer customer = new Customer();
        updateEntityFromRequest(request, customer);
        return customer;
    }

    public void updateEntityFromRequest(CustomerRequest request, Customer customer) {
        if (request == null || customer == null) {
            return;
        }

        // แยก name ออกเป็น firstName และ lastName
        if (request.getName() != null) {
            String[] parts = request.getName().trim().split("\\s+", 2);
            customer.setFirstName(parts[0]);
            customer.setLastName(parts.length > 1 ? parts[1] : "");
        }

        customer.setCitizenId(request.getCitizenId());
        customer.setPhone(request.getPhoneNumber()); // เปลี่ยนเป็น setPhone()
        customer.setAddress(request.getAddress());
    }
}
