package com.kku.pawnshop.repository;

import com.kku.pawnshop.domain.entity.Appraisal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** เจ้าของ: สมาชิก B */
@Repository
public interface AppraisalRepository extends JpaRepository<Appraisal, Long> {

    Optional<Appraisal> findByPledgedItemId(Long pledgedItemId);
}
