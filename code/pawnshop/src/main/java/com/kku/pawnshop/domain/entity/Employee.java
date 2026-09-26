package com.kku.pawnshop.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * พนักงาน — เจ้าของ: สมาชิก A
 *
 * ความสัมพันธ์ Many-to-Many กับ Role (พนักงาน 1 คนมีได้หลายบทบาท
 * และ 1 บทบาทมีพนักงานหลายคน) เป็นส่วนที่เก็บคะแนนพิเศษตามใบงาน
 */
@Entity
@Table(name = "employee", indexes = {
        @Index(name = "idx_employee_code", columnList = "employee_code", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_code", nullable = false, length = 20, unique = true)
    private String employeeCode;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    /**
     * FetchType.EAGER เพราะทุกครั้งที่หยิบพนักงานมาใช้ ต้องรู้บทบาทเพื่อเช็คสิทธิ์
     * และจำนวน role ต่อคนมีไม่เกิน 3 จึงไม่กระทบ performance
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "employee_role",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public boolean hasRole(String roleCode) {
        return roles.stream().anyMatch(r -> r.getCode().equals(roleCode));
    }
}
