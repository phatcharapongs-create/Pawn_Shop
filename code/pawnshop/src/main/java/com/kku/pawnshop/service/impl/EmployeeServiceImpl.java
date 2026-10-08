package com.kku.pawnshop.service.impl;

import com.kku.pawnshop.model.Employee;
import com.kku.pawnshop.repository.EmployeeRepository;
import com.kku.pawnshop.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Employee> getActiveEmployees() {
        return employeeRepository.findByIsActiveTrue();
    }
}
