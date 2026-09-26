package com.kku.pawnshop.repository;

import com.kku.pawnshop.domain.entity.PledgedItem;
import com.kku.pawnshop.domain.enums.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** เจ้าของ: สมาชิก B */
@Repository
public interface PledgedItemRepository extends JpaRepository<PledgedItem, Long> {

    List<PledgedItem> findBySerialNumber(String serialNumber);

    List<PledgedItem> findByItemType(ItemType itemType);
}
