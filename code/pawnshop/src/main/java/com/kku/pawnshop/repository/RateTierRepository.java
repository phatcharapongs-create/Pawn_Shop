package com.kku.pawnshop.repository;

import com.kku.pawnshop.domain.entity.RateTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** เจ้าของ: สมาชิก D */
@Repository
public interface RateTierRepository extends JpaRepository<RateTier, Long> {

    List<RateTier> findByPolicyIdOrderByTierOrderAsc(Long policyId);
}
