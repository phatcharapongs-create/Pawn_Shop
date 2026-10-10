package com.kku.pawnshop.service;

import com.kku.pawnshop.domain.entity.Employee;

import java.util.List;

public interface EmployeeService {

    List<Employee> findAll();

    List<Employee> getActiveEmployees();

    Employee findById(Long id);

    Employee create(Employee employee);

    Employee update(Long id, Employee employee);

    void delete(Long id);
}
