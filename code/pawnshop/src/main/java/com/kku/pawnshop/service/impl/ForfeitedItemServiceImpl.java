package com.kku.pawnshop.service.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kku.pawnshop.domain.entity.Customer;
import com.kku.pawnshop.domain.entity.PawnTicket;
import com.kku.pawnshop.domain.entity.PledgedItem;
import com.kku.pawnshop.domain.entity.SaleRecord;
import com.kku.pawnshop.domain.enums.TicketStatus;
import com.kku.pawnshop.domain.vo.Money;
import com.kku.pawnshop.dto.response.ForfeitedItemResponse;
import com.kku.pawnshop.dto.response.ForfeitedSummary;
import com.kku.pawnshop.dto.response.CustomerProductResponse;
import com.kku.pawnshop.repository.ForfeitedItemQueryRepository;
import com.kku.pawnshop.repository.PawnTicketRepository;
import com.kku.pawnshop.repository.SaleRecordRepository;
import com.kku.pawnshop.service.ForfeitedItemService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ForfeitedItemServiceImpl implements ForfeitedItemService {

    private final ForfeitedItemQueryRepository queryRepository;
    private final PawnTicketRepository ticketRepository;
    private final SaleRecordRepository saleRecordRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ForfeitedItemResponse> list(TicketStatus status, Pageable pageable) {
        Collection<TicketStatus> statuses = status != null
                ? List.of(status)
                : List.of(TicketStatus.FORFEITED, TicketStatus.SOLD);
        return queryRepository.findByStatusIn(statuses, pageable).map(this::toResponse);
    }

    /**
     * สรุปตัวเลข ใช้ repository เดิมของเพื่อนโดยไม่แก้ไฟล์ของเขา และรวมยอดใน Java
     * ปริมาณทรัพย์หลุดของโรงรับจำนำหนึ่งแห่งไม่มาก จึงยอมรับได้
     * ถ้าข้อมูลโตมาก ควรเปลี่ยนเป็น query แบบ SUM ในฐานข้อมูล
     */
    @Override
    @Transactional(readOnly = true)
    public ForfeitedSummary summary() {
        Money forfeitedPrincipal = ticketRepository.findByStatus(TicketStatus.FORFEITED, Pageable.unpaged())
                .stream()
                .map(PawnTicket::getPrincipal)
                .reduce(Money.zero(), Money::plus);

        Money soldTotal = saleRecordRepository.findAll().stream()
                .map(SaleRecord::getSoldPrice)
                .reduce(Money.zero(), Money::plus);

        return new ForfeitedSummary(
                ticketRepository.countByStatus(TicketStatus.FORFEITED),
                forfeitedPrincipal.getAmount(),
                ticketRepository.countByStatus(TicketStatus.SOLD),
                soldTotal.getAmount());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerProductResponse> listAvailableProducts(Pageable pageable) {
        return queryRepository.findByStatusIn(List.of(TicketStatus.FORFEITED), pageable)
                .map(ticket -> {
                    PledgedItem item = ticket.getPledgedItem();
                    return new CustomerProductResponse(
                            item.getItemType().name(), item.getDescription(), item.getPhotoUrl(),
                            item.getConditionGrade(), item.getWeightGram(), item.getPurityPercent(),
                            item.getManufactureYear());
                });
    }

    private ForfeitedItemResponse toResponse(PawnTicket ticket) {
        PledgedItem item = ticket.getPledgedItem();
        Customer customer = ticket.getCustomer();

        BigDecimal soldPrice = null;
        java.time.LocalDate soldDate = null;
        String buyerName = null;
        if (ticket.getStatus() == TicketStatus.SOLD) {
            SaleRecord sale = saleRecordRepository.findByPledgedItemId(item.getId()).orElse(null);
            if (sale != null) {
                soldPrice = sale.getSoldPrice().getAmount();
                soldDate = sale.getSoldDate();
                buyerName = sale.getBuyerName();
            }
        }

        return new ForfeitedItemResponse(
                ticket.getId(),
                ticket.getTicketNumber(),
                customer != null ? customer.getFirstName() + " " + customer.getLastName() : null,
                ticket.getPrincipal().getAmount(),
                ticket.getGraceEndDate(),
                ticket.getStatus().name(),
                item.getItemType().name(),
                item.getDescription(),
                item.getStorageSlot(),
                soldPrice,
                soldDate,
                buyerName);
    }
}
