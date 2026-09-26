package com.kku.pawnshop.service;

import com.kku.pawnshop.domain.entity.PledgedItem;

import java.util.List;

/** เจ้าของ: สมาชิก B */
public interface PledgedItemService {

    PledgedItem register(PledgedItem item);

    PledgedItem findById(Long id);

    List<PledgedItem> findForfeitedItemsForSale();
}
