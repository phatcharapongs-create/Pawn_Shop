package com.kku.pawnshop.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kku.pawnshop.domain.entity.PawnTicket;
import com.kku.pawnshop.domain.enums.TicketStatus;
import com.kku.pawnshop.domain.state.TicketState;
import com.kku.pawnshop.domain.state.TicketStateFactory;
import com.kku.pawnshop.event.TicketForfeitedEvent;
import com.kku.pawnshop.exception.BusinessRuleViolationException;
import com.kku.pawnshop.exception.InvalidTicketOperationException;
import com.kku.pawnshop.exception.ResourceNotFoundException;
import com.kku.pawnshop.repository.PawnTicketRepository;
import com.kku.pawnshop.service.TicketAdminService;

/**
 * คำสั่งระดับผู้จัดการ (อายัด / ปลดอายัด) และงานประจำวัน (เลื่อนสถานะตามวันที่)
 *
 * ISP: แยกจาก TicketOperationService เพราะเป็นสิทธิ์คนละกลุ่ม
 * controller ฝั่งพนักงานเคาน์เตอร์มองไม่เห็นเมธอดพวกนี้เลย
 */
@Service
@Transactional
public class TicketAdminServiceImpl implements TicketAdminService {

    private final PawnTicketRepository ticketRepository;
    private final TicketStateFactory stateFactory;
    private final ApplicationEventPublisher eventPublisher;

    public TicketAdminServiceImpl(PawnTicketRepository ticketRepository, TicketStateFactory stateFactory,
            ApplicationEventPublisher eventPublisher) {
        this.ticketRepository = ticketRepository;
        this.stateFactory = stateFactory;
        this.eventPublisher = eventPublisher;
    }

    /** อายัดได้เฉพาะตั๋วที่ยังเปิดอยู่ (คือยังไถ่ถอนได้) ตั๋วที่ปิดแล้วไม่มีอะไรให้อายัด */
    @Override
    public PawnTicket seize(Long ticketId, String reason, Long employeeId) {
        PawnTicket ticket = loadTicket(ticketId);
        TicketState state = stateFactory.stateOf(ticket.getStatus());

        if (!state.canRedeem()) {
            throw new InvalidTicketOperationException(ticket.getTicketNumber(), ticket.getStatus(), "อายัด");
        }
        if (reason == null || reason.isBlank()) {
            throw new BusinessRuleViolationException("ต้องระบุเหตุผลการอายัด");
        }

        ticket.setSeizedReason(reason);
        ticket.setStatus(TicketStatus.SEIZED);
        return ticketRepository.save(ticket);
    }

    /** ปลดอายัด: กลับไป ACTIVE ถ้ายังไม่เลยกำหนด ถ้าเลยแล้วกลับไป GRACE */
    @Override
    public PawnTicket releaseSeizure(Long ticketId, Long employeeId) {
        PawnTicket ticket = loadTicket(ticketId);

        if (ticket.getStatus() != TicketStatus.SEIZED) {
            throw new InvalidTicketOperationException(ticket.getTicketNumber(), ticket.getStatus(), "ปลดอายัด");
        }

        TicketStatus restored = ticket.isPastDue(LocalDate.now()) ? TicketStatus.GRACE : TicketStatus.ACTIVE;
        ticket.setStatus(restored);
        ticket.setSeizedReason(null);
        return ticketRepository.save(ticket);
    }

    /** งานประจำวัน: ตั๋ว ACTIVE ที่เลยวันครบกำหนดแล้ว → GRACE */
    @Override
    public List<PawnTicket> promoteOverdueToGrace(LocalDate asOf) {
        List<PawnTicket> overdue = ticketRepository.findByStatusAndDueDateBefore(TicketStatus.ACTIVE, asOf);
        overdue.forEach(t -> t.setStatus(TicketStatus.GRACE));
        return ticketRepository.saveAll(overdue);
    }

    /** งานประจำวัน: ตั๋ว GRACE ที่พ้นช่วงผ่อนผันแล้ว → FORFEITED และแจ้ง event */
    @Override
    public List<PawnTicket> forfeitExpired(LocalDate asOf) {
        List<PawnTicket> expired = ticketRepository.findByStatusAndGraceEndDateBefore(TicketStatus.GRACE, asOf);

        for (PawnTicket ticket : expired) {
            TicketState state = stateFactory.stateOf(ticket.getStatus());
            if (state.canForfeit()) {
                ticket.setStatus(state.nextAfterForfeit());
            }
        }
        List<PawnTicket> saved = ticketRepository.saveAll(expired);

        saved.forEach(t -> eventPublisher.publishEvent(new TicketForfeitedEvent(
                t.getId(), t.getTicketNumber(), t.getCustomer().getId(), t.getPledgedItem().getId())));
        return saved;
    }

    private PawnTicket loadTicket(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("ตั๋วจำนำ", ticketId));
    }
}
