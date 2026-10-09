# SOLID Analysis

ระบุว่าแต่ละหลักการปรากฏที่ไฟล์ไหน บรรทัดไหน พร้อมเหตุผล
path ทั้งหมดเริ่มจาก `code/pawnshop/src/main/java/com/kku/pawnshop/` (ยกเว้นที่ระบุว่าเป็น test)

> แต่ละคนเขียนเฉพาะใต้หัวข้อ slice ของตัวเอง เพื่อไม่ให้ชนกันตอน merge

---

## Slice A — ลูกค้าและพนักงาน (พิชญพงษ์)

_(รอเติม)_

## Slice B — ทรัพย์และการประเมินราคา (นันทกร)

_(รอเติม)_

## Slice C — ตั๋วจำนำและวงจรสถานะ (รัชนาท)

### S — Single Responsibility

| ไฟล์ | บรรทัด | เหตุผล |
|---|---|---|
| `service/impl/TicketQueryServiceImpl.java` | 18–19 | อ่านข้อมูลตั๋วอย่างเดียว (`@Transactional(readOnly = true)`) ไม่บันทึกอะไร |
| `service/impl/TicketOperationServiceImpl.java` | 50 | ทำธุรกรรมของตั๋ว (รับจำนำ ต่อดอก ไถ่ถอน) เท่านั้น ไม่คิดดอกเอง — ส่งให้ `InterestCalculator` (บรรทัด 54) และไม่ลงบัญชีเอง — ส่งให้ `LedgerService` (บรรทัด 146, 176) |
| `service/impl/TicketAdminServiceImpl.java` | 29 | คำสั่งผู้จัดการและงานประจำวันแยกออกมา เปลี่ยนนโยบายอายัดไม่กระทบธุรกรรมหน้าเคาน์เตอร์ |
| `mapper/TicketMapper.java` | 17, 25 | แปลง Entity → DTO ที่เดียว controller ไม่ต้องรู้โครงสร้าง entity |
| `controller/api/TicketRestController.java` | 43 | รับ HTTP แล้วส่งต่อ service ไม่มี business logic และไม่แตะ repository |

### O — Open/Closed

| ไฟล์ | บรรทัด | เหตุผล |
|---|---|---|
| `domain/state/TicketStateFactory.java` | 22–24 | ไม่มี `switch`/`if-else` ตามสถานะ Spring ฉีด `TicketState` ทุกตัวเข้ามาใน `List` แล้วสร้าง map จาก `status()` เพิ่มสถานะใหม่ = เพิ่มคลาสใหม่ 1 ไฟล์ ไม่แก้ factory |
| `service/impl/TicketOperationServiceImpl.java` | 141, 156, 169, 181 | ถาม `state.canRenew()` / `state.nextAfterRenew()` แทนการเช็คสถานะทีละค่า เพิ่มสถานะใหม่ service ไม่ต้องแก้ |
| `domain/entity/InterestPolicy.java` | 44, 48 | จำนวนเดือนไถ่ถอนและวันผ่อนผันเป็นข้อมูลในตาราง ไม่ฮาร์ดโค้ด กฎหมายเปลี่ยน → เพิ่มแถวใหม่ ไม่แก้โค้ด (ใช้ใน `TicketOperationServiceImpl` บรรทัด 107–109, 150–155) |

### L — Liskov Substitution

| ไฟล์ | บรรทัด | เหตุผล |
|---|---|---|
| `domain/state/TicketState.java` | 18–39 | ทุก state ตอบได้ทุกเมธอด ไม่มีตัวไหน throw exception — แยกเป็น `canXxx()` (ถามว่าทำได้ไหม) กับ `nextAfterXxx()` (ถ้าทำแล้วไปสถานะไหน) |
| `domain/state/SeizedState.java` | 20, 25 | สถานะที่ทำอะไรไม่ได้ ตอบ `false` และคืนสถานะเดิม (`SEIZED`) แทนการ throw `UnsupportedOperationException` |
| `service/impl/TicketOperationServiceImpl.java` | 141–142, 169–170 | การปฏิเสธคำสั่งเป็นหน้าที่ของ service ที่โยน `InvalidTicketOperationException` เอง ไม่ใช่ของ state |
| test: `domain/state/TicketStateTest.java` | 65 | `seizedState_statusCannotChange` พิสูจน์ว่าสั่งไถ่ถอนตั๋วที่ถูกอายัดแล้วไม่พัง แค่ได้สถานะเดิม |

### I — Interface Segregation

| ไฟล์ | บรรทัด | เหตุผล |
|---|---|---|
| `service/TicketQueryService.java` | 18 | การอ่านอย่างเดียว ใช้โดยหน้าจอดูสถานะตั๋ว |
| `service/TicketOperationService.java` | 15 | ธุรกรรมหน้าเคาน์เตอร์ |
| `service/TicketAdminService.java` | 13 | คำสั่งผู้จัดการและงานอัตโนมัติ |
| `controller/web/TicketWebController.java` | 35–36 | หน้าเว็บพนักงานถือแค่ Query + Operation **ไม่ได้ถือ** `TicketAdminService` จึงเรียกอายัดไม่ได้ตั้งแต่ระดับ type |

### D — Dependency Inversion

| ไฟล์ | บรรทัด | เหตุผล |
|---|---|---|
| `service/impl/TicketOperationServiceImpl.java` | 52–61, 63 | dependency ทุกตัวเป็น interface (`InterestCalculator`, `LedgerService`, `CustomerService`, `AppraisalService`, ...) และรับผ่าน constructor เท่านั้น ไม่มี `@Autowired` บนฟิลด์ |
| `service/impl/TicketQueryServiceImpl.java` | 20–21 | ขึ้นกับ `InterestCalculator` interface ไม่รู้ว่าข้างในคิดแบบขั้นบันได (`TieredInterestCalculator` ของ slice D) |
| `controller/api/TicketRestController.java` | 45–48 | controller ขึ้นกับ service interface ไม่ใช่คลาส `...Impl` |
| test: `service/impl/TicketOperationServiceImplTest.java` | 116, 146 | ผลของ DIP — ส่ง Mockito mock เข้า constructor แทนของจริงได้ เทสได้โดยไม่ต้องมีฐานข้อมูล |

## Slice D — บัญชีและดอกเบี้ย (พชรพงษ์)

_(รอเติม)_

## Slice E — ทรัพย์หลุด แจ้งเตือน และ DevOps (อนุชา)

_(รอเติม)_
