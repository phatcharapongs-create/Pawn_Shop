# โครงเริ่มต้น — ระบบจัดการโรงรับจำนำ

ไฟล์ชุดนี้คือสิ่งที่ต้อง commit ลง `develop` ในวันที่ 2-3 **ก่อนที่ทุกคนจะแยกย้ายไปเขียนโค้ดของตัวเอง**
หลังจาก merge ชุดนี้เข้า `develop` แล้ว ทั้ง 5 คนจะทำงานขนานกันได้โดยไม่ชนกัน

---

## วิธีนำไปใช้

1. สร้างโปรเจกต์จาก https://start.spring.io
   - Spring Boot 3.x, Java 17+, Maven
   - Dependencies: **Spring Web, Spring Data JPA, Thymeleaf, Validation, PostgreSQL Driver, Flyway Migration, Lombok, Spring Boot DevTools**
2. คัดลอกโฟลเดอร์ `src/` ทั้งก้อนทับลงในโปรเจกต์
3. ถ้าเปลี่ยนชื่อ package จาก `com.kku.pawnshop` ให้ใช้ Refactor > Rename ของ IDE ไม่ใช่ find-replace
4. เพิ่ม dependency ของ Swagger ลง `pom.xml`

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.6.0</version>
</dependency>
```

5. ตั้งค่า `application.properties` แล้วสั่งรัน Flyway จะสร้างตารางให้เอง

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/pawnshop
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true
spring.thymeleaf.cache=false
```

> `ddl-auto=validate` สำคัญมาก อย่าใช้ `update` เพราะจะทำให้ schema จริงไม่ตรงกับ migration
> แล้วตอน deploy ขึ้น cloud จะได้ตารางคนละแบบกับตอนรันในเครื่อง

---

## เช็ค Lombok ก่อนเริ่ม

โค้ดชุดนี้ใช้ `@Getter @Setter @NoArgsConstructor` ถ้าเปิดโปรเจกต์แล้วขึ้น error ว่า
`cannot find symbol: method getId()` แปลว่า annotation processing ยังไม่เปิด

- **IntelliJ**: Settings > Build > Compiler > Annotation Processors > ติ๊ก Enable
- **VS Code**: ติดตั้ง Extension Pack for Java แล้วรีโหลด

ถ้าแก้ไม่ได้จริง ๆ ให้กด Alt+Insert สร้าง getter/setter ทิ้งไว้แล้วลบ annotation ออก
อย่าปล่อยให้ทั้งทีมติดอยู่กับเรื่องนี้เกินครึ่งวัน

---

## ใครเป็นเจ้าของไฟล์ไหน

| สมาชิก | Entity | Repository | Service |
|---|---|---|---|
| **A** | Customer, Employee, Role | 3 ไฟล์ | CustomerService |
| **B** | PledgedItem, Appraisal, GoldPrice | 3 ไฟล์ | AppraisalService, PledgedItemService, **AppraisalStrategy 3 ตัว** |
| **C** (พีท) | PawnTicket | 1 ไฟล์ | TicketQuery/Operation/AdminService, **TicketState 6 ตัว** |
| **D** | LedgerEntry, InterestPolicy, RateTier | 3 ไฟล์ | LedgerService, **InterestCalculator, MonthFractionRule** |
| **E** | SaleRecord, NotificationLog | 2 ไฟล์ | SaleService, **NotificationListener (Observer)** |

ส่วนที่ตัวหนาคือของที่ยังเป็นแค่ interface ต้องไปเขียน implementation เอง

**งานกลางที่ต้องมีคนรับผิดชอบเพิ่ม**
- A: `GlobalExceptionHandler` + รูปแบบ error response + layout fragment ของ Thymeleaf
- E: Dockerfile, docker-compose, GitHub Actions, deploy, Swagger config

---

## สิ่งที่ยังไม่มีในชุดนี้ ต้องเขียนเอง

- implementation ของทุก service (`service/impl/`)
- `AppraisalStrategy` 3 ตัว: `GoldAppraisalStrategy`, `ElectronicsAppraisalStrategy`, `JewelryAppraisalStrategy`
- `TieredInterestCalculator` และ `HalfMonthFractionRule`
- `DbGoldPriceProvider` อ่านจากตาราง gold_price
- `NotificationListener` ที่ `@EventListener` ฟัง 3 event
- DTO และ Mapper ของแต่ละคน
- Controller ทั้งฝั่ง `api/` และ `web/`
- Unit test

---

## กฎที่ห้ามละเมิด (ใบงานหักคะแนน)

1. **Controller ห้ามเรียก Repository ตรง ๆ** ต้องผ่าน Service เสมอ
2. **Controller ห้าม return entity** ต้อง return DTO เสมอ ไม่งั้นเจอ `LazyInitializationException`
3. **ใช้ Constructor Injection เท่านั้น** ห้าม `@Autowired` บนฟิลด์
4. **State ห้ามโยน exception** การปฏิเสธคำสั่งเป็นหน้าที่ของ Service
5. **Resolver และ Factory ห้ามมี switch/if-else ตามประเภท** ใช้ `supports()` หรือ registry map
6. **ledger_entry ห้าม UPDATE และ DELETE** ถ้าผิดให้บันทึกรายการกลับรายการใหม่

---

## ตัวเลขที่ต้องตรวจสอบก่อนส่ง

ใน `V2__seed_reference_data.sql` มีอัตราดอกเบี้ย จำนวนเดือน และวันผ่อนผัน
ที่ใส่ไว้ให้ระบบรันได้เท่านั้น **ยังไม่ได้ตรวจสอบกับตัวบทกฎหมาย**

ต้องไปหา พ.ร.บ.โรงรับจำนำ ฉบับปัจจุบันมาตรวจสอบ แล้วแก้ตัวเลขให้ตรง
พร้อมอ้างอิงมาตราไว้ในรายงาน จะได้คะแนนส่วน domain analysis เพิ่มด้วย
