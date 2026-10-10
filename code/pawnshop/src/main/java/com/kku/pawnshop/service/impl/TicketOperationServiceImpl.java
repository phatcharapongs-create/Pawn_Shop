package com.kku.pawnshop.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kku.pawnshop.domain.entity.Appraisal;
import com.kku.pawnshop.domain.entity.Customer;
import com.kku.pawnshop.domain.entity.Employee;
import com.kku.pawnshop.domain.entity.InterestPolicy;
import com.kku.pawnshop.domain.entity.PawnTicket;
import com.kku.pawnshop.domain.entity.PledgedItem;
import com.kku.pawnshop.domain.enums.LedgerEntryType;
import com.kku.pawnshop.domain.enums.TicketStatus;
import com.kku.pawnshop.domain.state.TicketState;
import com.kku.pawnshop.domain.state.TicketStateFactory;
import com.kku.pawnshop.domain.vo.Money;
import com.kku.pawnshop.event.TicketRedeemedEvent;
import com.kku.pawnshop.exception.BusinessRuleViolationException;
import com.kku.pawnshop.exception.InvalidTicketOperationException;
import com.kku.pawnshop.exception.ResourceNotFoundException;
import com.kku.pawnshop.repository.EmployeeRepository;
import com.kku.pawnshop.repository.InterestPolicyRepository;
import com.kku.pawnshop.repository.PawnTicketRepository;
import com.kku.pawnshop.service.AppraisalService;
import com.kku.pawnshop.service.CustomerService;
import com.kku.pawnshop.service.LedgerService;
import com.kku.pawnshop.service.PledgedItemService;
import com.kku.pawnshop.service.TicketOperationService;
import com.kku.pawnshop.service.interest.InterestCalculator;

/**
 * ธุรกรรมของตั๋วจำนำ: รับจำนำ ต่อดอก ไถ่ถอน
 *
 * State Pattern: ทุกธุรกรรมถาม TicketState ก่อนว่า "ทำได้ไหม" (canXxx)
 * ถ้าไม่ได้ Service เป็นคนโยน InvalidTicketOperationException (→ 409)
 * ส่วน State ไม่เคยโยน exception เอง ตามข้อ LSP ในใบงาน
 *
 * DIP: ขึ้นกับ interface ทั้งหมด (service ของเพื่อน, calculator, repository)
 * และรับผ่าน constructor เท่านั้น
 *
 * Observer Pattern: ตอนไถ่ถอน publish TicketRedeemedEvent ออกไป
 * คลาสนี้ไม่รู้จักตัวส่งแจ้งเตือนเลย (ของอนุชาเป็นคนฟัง)
 */
@Service
@Transactional
public class TicketOperationServiceImpl implements TicketOperationService {

    private final PawnTicketRepository ticketRepository;
    private final TicketStateFactory stateFactory;
    private final InterestCalculator interestCalculator;
    private final LedgerService ledgerService;
    private final CustomerService customerService;
    private final PledgedItemService pledgedItemService;
    private final AppraisalService appraisalService;
    private final InterestPolicyRepository policyRepository;
    private final EmployeeRepository employeeRepository;
    private final ApplicationEventPublisher eventPublisher;

    public TicketOperationServiceImpl(PawnTicketRepository ticketRepository, TicketStateFactory stateFactory,
            InterestCalculator interestCalculator, LedgerService ledgerService, CustomerService customerService,
            PledgedItemService pledgedItemService, AppraisalService appraisalService,
            InterestPolicyRepository policyRepository, EmployeeRepository employeeRepository,
            ApplicationEventPublisher eventPublisher) {
        this.ticketRepository = ticketRepository;
        this.stateFactory = stateFactory;
        this.interestCalculator = interestCalculator;
        this.ledgerService = ledgerService;
        this.customerService = customerService;
        this.pledgedItemService = pledgedItemService;
        this.appraisalService = appraisalService;
        this.policyRepository = policyRepository;
        this.employeeRepository = employeeRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * รับจำนำ: ตรวจสิทธิ์ลูกค้า → ตรวจวงเงินกับผลประเมิน → ผูกนโยบายดอกเบี้ย ณ วันนี้ → ออกตั๋ว → ลงบัญชี
     */
    @Override
    public PawnTicket openTicket(Long customerId, Long pledgedItemId, Money requestedPrincipal, Long employeeId) {
        if (requestedPrincipal == null || requestedPrincipal.isZero() || requestedPrincipal.isNegative()) {
            throw new BusinessRuleViolationException("เงินต้นต้องมากกว่า 0");
        }

        customerService.assertEligibleToPawn(customerId);
        Customer customer = customerService.findById(customerId);
        PledgedItem item = pledgedItemService.findById(pledgedItemId);

        if (ticketRepository.existsByPledgedItemId(pledgedItemId)) {
            throw new BusinessRuleViolationException("ทรัพย์ชิ้นนี้ถูกออกตั๋วไปแล้ว");
        }

        // วงเงินที่ขอห้ามเกินวงเงินสูงสุดจากผลประเมิน (ของนันทกร)
        Appraisal appraisal = appraisalService.findByPledgedItemId(pledgedItemId);
        if (requestedPrincipal.isGreaterThan(appraisal.getMaxLoanAmount())) {
            throw new BusinessRuleViolationException(
                    "เงินต้นที่ขอ " + requestedPrincipal + " เกินวงเงินสูงสุด " + appraisal.getMaxLoanAmount());
        }

        LocalDate today = LocalDate.now();

        // ตั๋วผูกกับนโยบาย "ณ วันที่ออกตั๋ว" ถ้ากฎหมายเปลี่ยนทีหลัง ตั๋วใบนี้ยังคิดตามอัตราเดิม
        InterestPolicy policy = policyRepository.findEffectiveOn(today)
                .orElseThrow(() -> new BusinessRuleViolationException("ไม่พบนโยบายดอกเบี้ยที่บังคับใช้ ณ วันนี้"));
        LocalDate dueDate = today.plusMonths(policy.getRedemptionMonths());

        PawnTicket ticket = new PawnTicket();
        ticket.setTicketNumber(nextTicketNumber());
        ticket.setCustomer(customer);
        ticket.setPledgedItem(item);
        ticket.setInterestPolicy(policy);
        ticket.setOpenedBy(loadEmployee(employeeId));
        ticket.setPrincipal(requestedPrincipal);
        ticket.setPawnDate(today);
        ticket.setDueDate(dueDate);
        ticket.setGraceEndDate(dueDate.plusDays(policy.getGraceDays()));
        ticket.setInterestPaidUntil(today);
        ticket.setStatus(TicketStatus.ACTIVE);

        PawnTicket saved = ticketRepository.save(ticket);

        ledgerService.recordEntry(saved, LedgerEntryType.PAWN,
                requestedPrincipal, Money.zero(), requestedPrincipal,
                null, null, saved.getOpenedBy(), "รับจำนำ");

        return saved;
    }

    /**
     * ต่อดอก: คิดเงิน → ลงบัญชี → แก้ตั๋ว (ลำดับสำคัญ เพราะ from ต้องเป็น interestPaidUntil ตัวเดิม)
     */
    @Override
    public PawnTicket renewInterest(Long ticketId, LocalDate paymentDate, Long employeeId) {
        PawnTicket ticket = loadTicket(ticketId);
        TicketState state = stateFactory.stateOf(ticket.getStatus());

        if (!state.canRenew()) {
            throw new InvalidTicketOperationException(ticket.getTicketNumber(), ticket.getStatus(), "ต่อดอก");
        }

        // ป้องกันการส่งฟอร์มซ้ำ: การต่อดอกหนึ่งครั้งต่อตั๋วต่อวันเท่านั้น
        if (ledgerService.hasInterestPaymentOn(ticketId, LocalDate.now())) {
            throw new BusinessRuleViolationException(
                    "ตั๋วเลขที่ " + ticket.getTicketNumber() + " มีรายการชำระดอกเบี้ยในวันนี้แล้ว ไม่สามารถบันทึกซ้ำได้");
        }

        Money interest = interestCalculator.accruedInterest(ticket, paymentDate);
        ledgerService.recordEntry(ticket, LedgerEntryType.INTEREST_PAYMENT,
                Money.zero(), interest, interest,
                ticket.getInterestPaidUntil(), paymentDate, loadEmployee(employeeId), "ต่อดอก");

        InterestPolicy policy = ticket.getInterestPolicy();
        LocalDate newDueDate = paymentDate.plusMonths(policy.getRedemptionMonths());

        ticket.setInterestPaidUntil(paymentDate);
        ticket.setDueDate(newDueDate);
        ticket.setGraceEndDate(newDueDate.plusDays(policy.getGraceDays()));
        ticket.setStatus(state.nextAfterRenew());

        return ticketRepository.save(ticket);
    }

    /**
     * ไถ่ถอน: จ่ายเงินต้น + ดอกค้าง → ลงบัญชี → ปิดตั๋ว → แจ้ง event
     */
    @Override
    public PawnTicket redeem(Long ticketId, LocalDate paymentDate, Long employeeId) {
        PawnTicket ticket = loadTicket(ticketId);
        TicketState state = stateFactory.stateOf(ticket.getStatus());

        if (!state.canRedeem()) {
            throw new InvalidTicketOperationException(ticket.getTicketNumber(), ticket.getStatus(), "ไถ่ถอน");
        }

        Money interest = interestCalculator.accruedInterest(ticket, paymentDate);
        Money total = ticket.getPrincipal().plus(interest);

        ledgerService.recordEntry(ticket, LedgerEntryType.REDEMPTION,
                ticket.getPrincipal(), interest, total,
                ticket.getInterestPaidUntil(), paymentDate, loadEmployee(employeeId), "ไถ่ถอน");

        ticket.setInterestPaidUntil(paymentDate);
        ticket.setStatus(state.nextAfterRedeem());
        ticket.setClosedAt(LocalDateTime.now());

        PawnTicket saved = ticketRepository.save(ticket);

        eventPublisher.publishEvent(new TicketRedeemedEvent(
                saved.getId(), saved.getTicketNumber(), saved.getCustomer().getId(), total));

        return saved;
    }

    private PawnTicket loadTicket(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("ตั๋วจำนำ", ticketId));
    }

    /** พนักงานไม่บังคับ (null ได้) แต่ถ้าส่งรหัสมาแล้วหาไม่เจอ ถือว่าผิด */
    private Employee loadEmployee(Long employeeId) {
        if (employeeId == null) {
            return null;
        }
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("พนักงาน", employeeId));
    }

    /** เลขที่ตั๋วรูปแบบ PT000001 */
    private String nextTicketNumber() {
        return String.format("PT%06d", ticketRepository.count() + 1);
    }
}
