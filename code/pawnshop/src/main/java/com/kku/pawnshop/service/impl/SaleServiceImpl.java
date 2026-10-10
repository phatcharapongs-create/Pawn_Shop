package com.kku.pawnshop.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kku.pawnshop.domain.entity.Employee;
import com.kku.pawnshop.domain.entity.PawnTicket;
import com.kku.pawnshop.domain.entity.SaleRecord;
import com.kku.pawnshop.domain.state.TicketState;
import com.kku.pawnshop.domain.state.TicketStateFactory;
import com.kku.pawnshop.domain.vo.Money;
import com.kku.pawnshop.exception.BusinessRuleViolationException;
import com.kku.pawnshop.exception.InvalidTicketOperationException;
import com.kku.pawnshop.exception.ResourceNotFoundException;
import com.kku.pawnshop.repository.EmployeeRepository;
import com.kku.pawnshop.repository.PawnTicketRepository;
import com.kku.pawnshop.repository.SaleRecordRepository;
import com.kku.pawnshop.service.SaleService;

import lombok.RequiredArgsConstructor;

/**
 * จำหน่ายทรัพย์หลุดจำนำ
 *
 * State Pattern: ถาม TicketState ก่อนว่า canSell() ไหม ถ้าไม่ได้ Service เป็นคนโยน
 * InvalidTicketOperationException เอง (State ไม่โยน ตามข้อ LSP ในใบงาน)
 * สถานะใหม่หลังขายมาจาก state.nextAfterSell() ไม่ได้ฮาร์ดโค้ด SOLD ไว้ที่นี่
 */
@Service
@Transactional
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final PawnTicketRepository ticketRepository;
    private final SaleRecordRepository saleRecordRepository;
    private final EmployeeRepository employeeRepository;
    private final TicketStateFactory stateFactory;

    @Override
    public SaleRecord recordSale(Long ticketId, Money soldPrice, String buyerName,
                                 LocalDate soldDate, Long employeeId) {

        PawnTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("ตั๋วจำนำ", ticketId));

        TicketState state = stateFactory.stateOf(ticket.getStatus());
        if (!state.canSell()) {
            throw new InvalidTicketOperationException(ticket.getTicketNumber(), ticket.getStatus(), "จำหน่ายทรัพย์");
        }
        if (soldPrice == null || !soldPrice.isGreaterThan(Money.zero())) {
            throw new BusinessRuleViolationException("ราคาขายต้องมากกว่า 0");
        }
        if (saleRecordRepository.findByPledgedItemId(ticket.getPledgedItem().getId()).isPresent()) {
            throw new BusinessRuleViolationException("ทรัพย์ชิ้นนี้ถูกบันทึกการขายไปแล้ว");
        }

        Employee employee = null;
        if (employeeId != null) {
            employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new ResourceNotFoundException("พนักงาน", employeeId));
        }

        SaleRecord sale = new SaleRecord();
        sale.setPledgedItem(ticket.getPledgedItem());
        sale.setSoldPrice(soldPrice);
        sale.setSoldDate(soldDate != null ? soldDate : LocalDate.now());
        sale.setBuyerName(buyerName == null || buyerName.isBlank() ? null : buyerName.trim());
        sale.setHandledBy(employee);
        SaleRecord saved = saleRecordRepository.save(sale);

        ticket.setStatus(state.nextAfterSell());
        if (ticket.getClosedAt() == null) {
            ticket.setClosedAt(LocalDateTime.now());
        }
        ticketRepository.save(ticket);

        return saved;
    }
}
