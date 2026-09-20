# ระบบจัดการโรงรับจำนำ (Pawnshop Management System)
 
ระบบสารสนเทศสำหรับบริหารงานโรงรับจำนำ ครอบคลุมตั้งแต่การประเมินราคาทรัพย์จำนำตามประเภท
การออกตั๋วจำนำ การรับชำระดอกเบี้ยและต่อดอก การไถ่ถอน ไปจนถึงการจัดการทรัพย์หลุดจำนำ
ออกแบบให้รองรับการคำนวณดอกเบี้ยแบบขั้นบันไดตามที่กฎหมายกำหนด
และรองรับการเปลี่ยนแปลงอัตราดอกเบี้ยในอนาคตโดยไม่กระทบโครงสร้างระบบเดิม
 
รายวิชา CP353002 Principles of Software Design and Development
 
---
 
## สมาชิกกลุ่ม
 
| ลำดับ | ชื่อ-นามสกุล | รหัสนักศึกษา | Section | Branch | หน้าที่รับผิดชอบ |
|---|---|---|---|---|---|
| 1 | พิชญพงษ์ ทองแม้น | 673380595-7 | 3 | `pitchayapong_6733805957_03` | ลูกค้าและพนักงาน (customer, employee, role), Global Exception Handler, DTO/Mapper convention, Thymeleaf layout กลาง |
| 2 | นันทกร แสวงจิตร | 673380085-0 | 3 | `nantakorn_6733800850_03` | ทรัพย์จำนำและการประเมินราคา (pledged_item, appraisal, gold_price), **Strategy Pattern** |
| 3 | รัชนาท ประเสริฐศิลป์ | 673380600-0 | 3 | `ratchanat_6733806000_03` | ตั๋วจำนำและวงจรสถานะ (pawn_ticket), **State Pattern**, ธุรกรรมต่อดอกและไถ่ถอน |
| 4 | พชรพงษ์ สาหล่อน | 673380414-7 | 3 | `phatcharapong_6733804147_03` | สมุดบัญชีธุรกรรม (ledger_entry), นโยบายและการคำนวณดอกเบี้ยขั้นบันได (interest_policy, rate_tier) |
| 5 | อนุชา ประมาระตา | 673380607-6 | 3 | `anucha_6733806076_03` | ทรัพย์หลุดและการแจ้งเตือน (sale_record, notification_log), **Observer Pattern**, Docker, CI/CD, Deployment, Swagger |
 
---
 
## Tech Stack
 
| หมวด | เทคโนโลยี |
|---|---|
| Backend | Spring Boot 3.x (Java 17) |
| Build Tool | Maven |
| Database | PostgreSQL 15 |
| ORM | Spring Data JPA (Hibernate) |
| Migration | Flyway |
| Frontend | Thymeleaf + Bootstrap 5 |
| API Docs | springdoc-openapi (Swagger UI) |
| Testing | JUnit 5 + Mockito + Spring Boot Test |
| Container | Docker, Docker Compose |
| CI/CD | GitHub Actions |
| Deployment | _(รอระบุ)_ |
 
---
 
## System Architecture
 
ใช้ Layered Architecture แบ่ง 4 ชั้น ห้ามข้าม Layer
 
```
Presentation Layer   controller/web (Thymeleaf) + controller/api (REST)
        |
Service Layer        business logic + transaction management
        |
Repository Layer     Spring Data JPA
        |
Domain Layer         entity, value object, enum, state
```
 
**กฎที่ห้ามละเมิด**
 
1. Controller ห้ามเรียก Repository ตรง ๆ ต้องผ่าน Service เสมอ
2. Controller ห้าม return entity ต้อง return DTO เสมอ
3. ใช้ Constructor Injection เท่านั้น ห้าม `@Autowired` บนฟิลด์
4. `ledger_entry` เป็น append-only ห้าม UPDATE และ DELETE
รายละเอียดการวิเคราะห์ SOLID อยู่ใน `doc/solid-analysis.md`
รายละเอียด Design Pattern อยู่ใน `doc/design-patterns.md`
 
---
 
## Database Design (ER Diagram)
 
ER Diagram อยู่ที่ `doc/diagrams/er-diagram.png`
Data Dictionary อยู่ที่ `doc/data-dictionary.md`
 
**ตารางทั้งหมด 12 ตาราง**
 
| ตาราง | คำอธิบาย | ความสัมพันธ์ที่สำคัญ |
|---|---|---|
| `customer` | ผู้จำนำ | 1:N → `pawn_ticket` |
| `employee` | พนักงาน | M:N ↔ `role` |
| `role` | บทบาทพนักงาน | |
| `employee_role` | ตารางเชื่อม M:N | |
| `pledged_item` | ทรัพย์ที่นำมาจำนำ | 1:1 → `appraisal` |
| `appraisal` | ผลการประเมินราคา | |
| `gold_price` | ราคาทองรายวัน | |
| `pawn_ticket` | ตั๋วจำนำ | 1:1 → `pledged_item`, 1:N → `ledger_entry` |
| `ledger_entry` | รายการธุรกรรม (append-only) | |
| `interest_policy` | นโยบายอัตราดอกเบี้ย | 1:N → `rate_tier` |
| `rate_tier` | ขั้นอัตราดอกเบี้ย | |
| `sale_record` | บันทึกการขายทรัพย์หลุด | 1:1 → `pledged_item` |
| `notification_log` | บันทึกการแจ้งเตือน | |
 
Schema สร้างโดย Flyway อัตโนมัติจาก `code/src/main/resources/db/migration/`
 
---
 
## Installation & Setup
 
**สิ่งที่ต้องมีในเครื่อง**
 
- JDK 17 ขึ้นไป
- Maven 3.8 ขึ้นไป
- PostgreSQL 15 หรือ Docker
**1. Clone โปรเจกต์**
 
```bash
git clone https://github.com/phatcharapongs-create/Pawn_Shop.git
cd Pawn_Shop/code
```
 
**2. เตรียมฐานข้อมูล**
 
```bash
docker compose up -d db
```
 
หรือถ้าใช้ PostgreSQL ที่ลงไว้เองให้สร้างฐานข้อมูลชื่อ `pawnshop`
 
**3. ตั้งค่าการเชื่อมต่อ**
 
แก้ `src/main/resources/application.properties` หรือกำหนดเป็น environment variable
 
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/pawnshop
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true
```
 
> ใช้ `ddl-auto=validate` เท่านั้น ห้ามใช้ `update`
> เพราะจะทำให้ schema ในเครื่องไม่ตรงกับที่ Flyway สร้างบน server
 
---
 
## How to Run
 
```bash
cd code
./mvnw spring-boot:run
```
 
เปิดที่ http://localhost:8080
 
หรือรันทั้งระบบพร้อมฐานข้อมูลด้วย Docker
 
```bash
docker compose up --build
```
 
**หน้าจอหลัก**
 
| URL | หน้าจอ |
|---|---|
| `/tickets/new` | รับจำนำและออกตั๋ว |
| `/tickets` | ค้นหาตั๋วและดูรายละเอียด |
| `/customers` | รายชื่อลูกค้า |
| `/reports/daily` | รายงานประจำวัน |
 
---
 
## API Documentation
 
Swagger UI: http://localhost:8080/swagger-ui.html
 
**Endpoint หลัก**
 
| Method | Endpoint | คำอธิบาย |
|---|---|---|
| GET | `/api/v1/customers` | รายชื่อลูกค้า (มี pagination และ sorting) |
| POST | `/api/v1/customers` | เพิ่มลูกค้าใหม่ |
| GET | `/api/v1/customers/{id}` | ดูข้อมูลลูกค้า |
| PUT | `/api/v1/customers/{id}` | แก้ไขข้อมูลลูกค้า |
| DELETE | `/api/v1/customers/{id}` | ลบลูกค้า |
| GET | `/api/v1/customers/{id}/tickets` | ตั๋วทั้งหมดของลูกค้า |
| GET | `/api/v1/tickets` | รายการตั๋ว |
| POST | `/api/v1/tickets` | รับจำนำและออกตั๋วใหม่ |
| GET | `/api/v1/tickets/{id}` | รายละเอียดตั๋ว |
| POST | `/api/v1/tickets/{id}/renewals` | ต่อดอก |
| POST | `/api/v1/tickets/{id}/redemption` | ไถ่ถอน |
 
**HTTP Status Code ที่ใช้**
 
| Code | กรณี |
|---|---|
| 200 | สำเร็จ |
| 201 | สร้างข้อมูลใหม่สำเร็จ |
| 204 | ลบสำเร็จ |
| 400 | ข้อมูลไม่ผ่าน validation หรือผิดกฎธุรกิจ |
| 404 | ไม่พบข้อมูล |
| 409 | ทำธุรกรรมกับตั๋วที่อยู่ในสถานะที่ทำไม่ได้ |
| 500 | ข้อผิดพลาดของระบบ |
 
---
 
## How to Run Tests
 
```bash
cd code
./mvnw test
```
 
ดูรายงานผลการทดสอบได้ที่ `code/target/surefire-reports/`
สรุปผลการทดสอบอยู่ที่ `test/test-report.md`
 
---
 
## Deployment URL
 
_(รอ deploy — ต้องใช้งานได้จริง ณ วันนำเสนอ)_
 
---
 
## Project Structure
 
```
Pawn_Shop/
├── code/                          Source code
│   └── src/main/java/com/kku/pawnshop/
│       ├── config/
│       ├── controller/
│       │   ├── api/               RestController
│       │   └── web/               Thymeleaf Controller
│       ├── service/
│       │   ├── impl/
│       │   ├── appraisal/         Strategy Pattern
│       │   ├── interest/          การคำนวณดอกเบี้ย
│       │   └── pricing/
│       ├── repository/
│       ├── domain/
│       │   ├── entity/
│       │   ├── enums/
│       │   ├── vo/                Value Object
│       │   └── state/             State Pattern
│       ├── dto/
│       │   ├── request/
│       │   └── response/
│       ├── mapper/
│       ├── event/                 Observer Pattern
│       ├── exception/
│       └── common/
├── test/                          รายงานผลการทดสอบ
├── doc/
│   ├── diagrams/                  UML ทุกชนิด
│   ├── slide/                     สไลด์นำเสนอ
│   ├── solid-analysis.md
│   ├── design-patterns.md
│   └── data-dictionary.md
└── img/                           ภาพประกอบ
```
 
---
 
## Git Workflow
 
```
<ชื่อ>_<รหัสนักศึกษา>_03   →  develop  →  main
```
 
- แต่ละคนทำงานบน branch ของตัวเองเท่านั้น
- Merge เข้า `develop` ผ่าน Pull Request และต้องมี reviewer อย่างน้อย 1 คน
- `main` merge เฉพาะเวอร์ชันที่ส่งมอบ
- ทุกคน commit ด้วยบัญชี GitHub ของตนเองเท่านั้น
**Commit message convention**
 
```
feat:     เพิ่มฟีเจอร์ใหม่
fix:      แก้บั๊ก
refactor: ปรับโครงสร้างโค้ดโดยไม่เปลี่ยนพฤติกรรม
test:     เพิ่มหรือแก้ไขการทดสอบ
docs:     แก้ไขเอกสาร
```
 
**ก่อนเปิด Pull Request ทุกครั้ง**
 
```bash
git checkout develop
git pull
git checkout <branch ของตัวเอง>
git merge develop
```
