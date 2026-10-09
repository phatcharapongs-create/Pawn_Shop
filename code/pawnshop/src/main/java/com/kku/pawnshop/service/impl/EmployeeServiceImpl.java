package com.kku.pawnshop.service.impl;

import com.kku.pawnshop.domain.entity.Employee;
import com.kku.pawnshop.exception.ResourceNotFoundException;
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
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> getActiveEmployees() { // หากใน Interface ใช้ชื่อ findActive() สามารถเปลี่ยนชื่อเมธอดตรงนี้ให้ตรงกันได้ครับ
        return employeeRepository.findByActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบข้อมูลพนักงาน ID: " + id));
    }

    @Override
    @Transactional
    public Employee create(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public Employee update(Long id, Employee employee) {
        Employee existing = findById(id);
        existing.setFirstName(employee.getFirstName());
        existing.setLastName(employee.getLastName());
        existing.setPhone(employee.getPhone());
        existing.setActive(employee.isActive());
        return employeeRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Employee employee = findById(id);
        employeeRepository.delete(employee);
    }
}
