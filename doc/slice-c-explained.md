# Slice C — ตั๋วจำนำ: อธิบายโค้ดสำหรับเตรียมตอบอาจารย์

## ภาพรวม flow

```
หน้าเว็บ/API  →  TicketWebController / TicketRestController   (Presentation)
                       ↓ เรียกผ่าน interface เท่านั้น
               TicketQueryService      อ่านอย่างเดียว
               TicketOperationService  รับจำนำ ต่อดอก ไถ่ถอน
               TicketAdminService      อายัด ปลดอายัด งานประจำวัน      (Service)
                       ↓
               PawnTicketRepository + TicketStateFactory               (Repository / Domain)
```

## ไฟล์และหน้าที่

| ไฟล์ | หน้าที่ |
|---|---|
| `service/impl/TicketQueryServiceImpl` | หาตั๋ว, ยอดต่อดอก/ไถ่ถอน (เขียนเองแล้ว) |
| `service/impl/TicketOperationServiceImpl` | openTicket, renewInterest, redeem |
| `service/impl/TicketAdminServiceImpl` | seize, releaseSeizure, promoteOverdueToGrace, forfeitExpired |
| `mapper/TicketMapper` | Entity → DTO และใส่ canRenew/canRedeem ให้หน้าเว็บ |
| `dto/request/*`, `dto/response/*` | สัญญาของ API แยกจาก Entity |
| `controller/api/TicketRestController` | `/api/v1/tickets/...` |
| `controller/web/TicketWebController` | `/pawn-tickets/...` (ตรงกับเมนูใน layout) |
| `templates/pawn-tickets/*.html` | รายการ / รับจำนำ / รายละเอียด |

## ทุกธุรกรรมมีรูปแบบเดียวกัน

1. หาตั๋ว (`loadTicket`) — ไม่เจอ → `ResourceNotFoundException` → 404
2. ขอ state จาก factory แล้วถาม `canXxx()` — ไม่ได้ → service โยน `InvalidTicketOperationException` → 409
3. คิดเงินผ่าน `InterestCalculator`
4. ลงบัญชีผ่าน `LedgerService.recordEntry` (ledger เป็น append-only)
5. แก้ตั๋ว และสถานะใหม่มาจาก `state.nextAfterXxx()` ไม่ใช่ if-else
6. `save` — ทั้งหมดอยู่ใน `@Transactional` พังกลางทางยกเลิกทั้งก้อน

## คำถามที่น่าจะโดน + คำตอบ

**ทำไม State ไม่โยน exception เอง?**
ใบงานข้อ LSP ห้าม subclass โยน exception ทุก state ต้องเอาไปแทนกันได้ state จึงแค่ตอบ true/false ส่วนการปฏิเสธเป็นหน้าที่ของ service

**ทำไมลงบัญชีก่อนแก้ตั๋ว?**
ช่วงดอก `from` ต้องเป็น `interestPaidUntil` ตัวเดิม ถ้าแก้ตั๋วก่อน from กับ to จะเป็นวันเดียวกัน ตรวจย้อนหลังไม่ได้

**ทำไมเดือนไถ่ถอนกับวันผ่อนผันไม่ฮาร์ดโค้ด?**
ดึงจาก `InterestPolicy` ที่ผูกกับตั๋ว ณ วันออก ถ้ากฎหมายเปลี่ยน เพิ่มแถวใหม่ในตาราง ไม่ต้องแก้โค้ด (OCP) และตั๋วเก่ายังคิดตามนโยบายเดิม

**Observer อยู่ตรงไหน?**
`redeem` และ `forfeitExpired` เรียก `eventPublisher.publishEvent(...)` service ไม่รู้ว่าใครฟัง ของอนุชาเป็น listener เพิ่มช่องทางแจ้งเตือนใหม่ไม่ต้องแก้ service

**ISP?**
แยก Query / Operation / Admin เป็น 3 interface หน้าเว็บพนักงานไม่ได้ถือ `TicketAdminService` เลย จึงเรียกอายัดไม่ได้ตั้งแต่ระดับ type

**DIP?**
ทุก dependency เป็น interface และรับผ่าน constructor ตอนเทสจึงส่ง Mockito mock เข้าไปแทนได้

**ทำไม openTicket ตรวจ `maxLoanAmount`?**
วงเงินห้ามเกินผลประเมิน × LTV ตามประเภททรัพย์ (Strategy ของนันทกร)

**ทำไม promoteOverdueToGrace ตั้ง GRACE ตรง ๆ ไม่ผ่าน state?**
การเลยกำหนดเป็นผลของเวลา ไม่ใช่คำสั่งจากผู้ใช้ จึงไม่มีเมธอด `nextAfter...` ให้ — ยอมรับได้ ถ้าอยากให้ครบต้องเพิ่ม `nextAfterOverdue()` ใน TicketState

**ทำไม API ต่อดอกเป็น POST /tickets/{id}/renewals?**
ต่อดอกไม่ใช่การแก้ตั๋ว (PUT) แต่เป็นการสร้างรายการธุรกรรมใหม่ จึงเป็น sub-resource ที่ POST สร้าง → 201

## เทส

- `TicketOperationServiceImplTest` 8 เทส — ใช้ TicketStateFactory ตัวจริง ที่เหลือ mock
- `TicketAdminServiceImplTest` 6 เทส
- ใช้ `verify(...)` เช็คว่าลงบัญชีจริง และ `verifyNoInteractions(...)` เช็คว่าตอนถูกปฏิเสธ **ไม่มี** การลงบัญชีหรือส่ง event เลย
