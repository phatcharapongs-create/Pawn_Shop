package com.kku.pawnshop.service;

import com.kku.pawnshop.domain.entity.Appraisal;
import com.kku.pawnshop.domain.entity.PledgedItem;

/** เจ้าของ: สมาชิก B */
public interface AppraisalService {

    /** ประเมินราคาทรัพย์และบันทึกผล พร้อม snapshot ราคาทองถ้าเป็นทอง */
    Appraisal appraise(PledgedItem item, Long appraiserId);

    Appraisal findByPledgedItemId(Long pledgedItemId);
}
