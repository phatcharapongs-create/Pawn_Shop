package com.kku.pawnshop.service;

import com.kku.pawnshop.domain.entity.Appraisal;
import com.kku.pawnshop.domain.entity.PledgedItem;
import com.kku.pawnshop.dto.pledged.AppraisalHistoryRow;
import com.kku.pawnshop.dto.pledged.AppraisalDetailView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** เจ้าของ: สมาชิก B */
public interface AppraisalService {

    /** ประเมินราคาทรัพย์และบันทึกผล พร้อม snapshot ราคาทองถ้าเป็นทอง */
    Appraisal appraise(PledgedItem item, Long appraiserId);

    Appraisal findByPledgedItemId(Long pledgedItemId);

    Page<AppraisalHistoryRow> findHistory(Pageable pageable);

    Appraisal findById(Long id);

    AppraisalDetailView findDetail(Long id);
}
