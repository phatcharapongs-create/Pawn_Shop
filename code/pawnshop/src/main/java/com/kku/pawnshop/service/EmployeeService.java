package com.kku.pawnshop.service;

import com.kku.pawnshop.model.Employee;
import java.util.List;

public interface EmployeeService {
    List<Employee> getActiveEmployees();
}
