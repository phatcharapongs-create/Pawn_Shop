package com.kku.pawnshop.repository;

import com.kku.pawnshop.domain.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {
}