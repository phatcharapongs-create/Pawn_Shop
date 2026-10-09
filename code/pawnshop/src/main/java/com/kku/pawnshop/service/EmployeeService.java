package com.kku.pawnshop.service;

import com.kku.pawnshop.domain.entity.Employee; // แก้ไขตำแหน่ง 
import com.kku.pawnshop.dto.request.EmployeeRequest;
import com.kku.pawnshop.dto.response.EmployeeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {
    Page<EmployeeResponse> getAllEmployees(Pageable pageable);
    EmployeeResponse getEmployeeById(Long id);
    EmployeeResponse createEmployee(EmployeeRequest request);
    EmployeeResponse updateEmployee(Long id, EmployeeRequest request);
    void deleteEmployee(Long id);
}
