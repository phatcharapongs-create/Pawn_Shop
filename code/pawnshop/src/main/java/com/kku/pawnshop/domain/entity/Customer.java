package com.kku.pawnshop.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** ผู้จำนำ — เจ้าของ: สมาชิก A */
@Entity
@Table(name = "customer", indexes = {
        @Index(name = "idx_customer_citizen_id", columnList = "citizen_id", unique = true),
        @Index(name = "idx_customer_phone", columnList = "phone")
})
@Getter
@Setter
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "citizen_id", nullable = false, length = 13, unique = true)
    private String citizenId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    /** ลูกค้าที่ถูกระงับสิทธิ์จำนำ เช่น เคยนำทรัพย์ต้องสงสัยมาจำนำ */
    @Column(name = "blacklisted", nullable = false)
    private boolean blacklisted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
