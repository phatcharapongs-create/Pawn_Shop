# Data Dictionary

ที่มา: `code/pawnshop/src/main/resources/db/migration/V1__init_schema.sql` และ `V20261009001350__add_item_reference_price.sql`

## `customer` — ลูกค้า

| คอลัมน์ | ชนิด | เงื่อนไข |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `citizen_id` | VARCHAR(13) | NOT NULL UNIQUE |
| `first_name` | VARCHAR(100) | NOT NULL |
| `last_name` | VARCHAR(100) | NOT NULL |
| `phone` | VARCHAR(20) | - |
| `address` | VARCHAR(500) | - |
| `birth_date` | DATE | - |
| `blacklisted` | BOOLEAN | NOT NULL DEFAULT FALSE |
| `created_at` | TIMESTAMP | NOT NULL |

## `role` — บทบาทพนักงาน

| คอลัมน์ | ชนิด | เงื่อนไข |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `code` | VARCHAR(30) | NOT NULL UNIQUE |
| `name` | VARCHAR(100) | NOT NULL |

## `employee` — พนักงาน

| คอลัมน์ | ชนิด | เงื่อนไข |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `employee_code` | VARCHAR(20) | NOT NULL UNIQUE |
| `first_name` | VARCHAR(100) | NOT NULL |
| `last_name` | VARCHAR(100) | NOT NULL |
| `active` | BOOLEAN | NOT NULL DEFAULT TRUE |

## `employee_role` — ตารางเชื่อม พนักงาน–บทบาท (M:N)

| คอลัมน์ | ชนิด | เงื่อนไข |
|---|---|---|
| `employee_id` | BIGINT | NOT NULL FK → employee.id (ON DELETE CASCADE) |
| `role_id` | BIGINT | NOT NULL FK → role.id (ON DELETE RESTRICT) |

## `pledged_item` — ทรัพย์จำนำ

| คอลัมน์ | ชนิด | เงื่อนไข |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `item_type` | VARCHAR(20) | NOT NULL |
| `description` | VARCHAR(300) | NOT NULL |
| `serial_number` | VARCHAR(100) | - |
| `weight_gram` | NUMERIC(10,3) | - |
| `purity_percent` | NUMERIC(5,2) | - |
| `manufacture_year` | INTEGER | - |
| `condition_grade` | INTEGER | - |
| `storage_slot` | VARCHAR(30) | - |
| `photo_url` | VARCHAR(300) | - |
| `reference_price` | NUMERIC(15,2) | (เพิ่มใน migration V20261009001350) |

## `gold_price` — ราคาทองรายวัน

| คอลัมน์ | ชนิด | เงื่อนไข |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `price_date` | DATE | NOT NULL UNIQUE |
| `price_per_gram` | NUMERIC(15,2) | NOT NULL |
| `recorded_by` | BIGINT | FK → employee.id (ON DELETE SET NULL) |

## `appraisal` — ผลประเมินราคา (1:1 กับทรัพย์)

| คอลัมน์ | ชนิด | เงื่อนไข |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `pledged_item_id` | BIGINT | NOT NULL FK → pledged_item.id (ON DELETE CASCADE) UNIQUE |
| `appraiser_id` | BIGINT | FK → employee.id (ON DELETE SET NULL) |
| `appraised_value` | NUMERIC(15,2) | NOT NULL |
| `max_loan_amount` | NUMERIC(15,2) | NOT NULL |
| `gold_price_snapshot` | NUMERIC(15,2) | - |
| `appraised_at` | TIMESTAMP | NOT NULL |
| `note` | VARCHAR(500) | - |

## `interest_policy` — นโยบายดอกเบี้ย

| คอลัมน์ | ชนิด | เงื่อนไข |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `code` | VARCHAR(30) | NOT NULL UNIQUE |
| `name` | VARCHAR(150) | NOT NULL |
| `effective_from` | DATE | NOT NULL |
| `effective_to` | DATE | - |
| `redemption_months` | INTEGER | NOT NULL |
| `grace_days` | INTEGER | NOT NULL |

## `rate_tier` — ขั้นอัตราดอกเบี้ยของนโยบาย

| คอลัมน์ | ชนิด | เงื่อนไข |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `policy_id` | BIGINT | NOT NULL FK → interest_policy.id (ON DELETE CASCADE) |
| `lower_bound` | NUMERIC(15,2) | NOT NULL |
| `upper_bound` | NUMERIC(15,2) | - |
| `monthly_rate_percent` | NUMERIC(6,4) | NOT NULL |
| `tier_order` | INTEGER | NOT NULL |

## `pawn_ticket` — ตั๋วจำนำ

| คอลัมน์ | ชนิด | เงื่อนไข |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `ticket_number` | VARCHAR(20) | NOT NULL UNIQUE |
| `customer_id` | BIGINT | NOT NULL FK → customer.id (ON DELETE RESTRICT) |
| `pledged_item_id` | BIGINT | NOT NULL FK → pledged_item.id (ON DELETE RESTRICT) UNIQUE |
| `interest_policy_id` | BIGINT | NOT NULL FK → interest_policy.id (ON DELETE RESTRICT) |
| `opened_by` | BIGINT | FK → employee.id (ON DELETE SET NULL) |
| `principal` | NUMERIC(15,2) | NOT NULL |
| `pawn_date` | DATE | NOT NULL |
| `due_date` | DATE | NOT NULL |
| `grace_end_date` | DATE | NOT NULL |
| `interest_paid_until` | DATE | NOT NULL |
| `status` | VARCHAR(20) | NOT NULL |
| `closed_at` | TIMESTAMP | - |
| `seized_reason` | VARCHAR(300) | - |
| `created_at` | TIMESTAMP | NOT NULL |
| `updated_at` | TIMESTAMP | NOT NULL |

## `ledger_entry` — สมุดบัญชีธุรกรรม (append-only)

| คอลัมน์ | ชนิด | เงื่อนไข |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `ticket_id` | BIGINT | NOT NULL FK → pawn_ticket.id (ON DELETE RESTRICT) |
| `entry_type` | VARCHAR(30) | NOT NULL |
| `principal_amount` | NUMERIC(15,2) | NOT NULL |
| `interest_amount` | NUMERIC(15,2) | NOT NULL |
| `total_amount` | NUMERIC(15,2) | NOT NULL |
| `interest_from` | DATE | - |
| `interest_to` | DATE | - |
| `entry_date` | DATE | NOT NULL |
| `handled_by` | BIGINT | FK → employee.id (ON DELETE SET NULL) |
| `note` | VARCHAR(300) | - |
| `created_at` | TIMESTAMP | NOT NULL |

## `sale_record` — การขายทรัพย์หลุดจำนำ

| คอลัมน์ | ชนิด | เงื่อนไข |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `pledged_item_id` | BIGINT | NOT NULL FK → pledged_item.id (ON DELETE RESTRICT) UNIQUE |
| `sold_price` | NUMERIC(15,2) | NOT NULL |
| `sold_date` | DATE | NOT NULL |
| `buyer_name` | VARCHAR(200) | - |
| `handled_by` | BIGINT | FK → employee.id (ON DELETE SET NULL) |

## `notification_log` — ประวัติการแจ้งเตือน

| คอลัมน์ | ชนิด | เงื่อนไข |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `ticket_id` | BIGINT | FK → pawn_ticket.id (ON DELETE CASCADE) |
| `channel` | VARCHAR(20) | NOT NULL |
| `status` | VARCHAR(20) | NOT NULL |
| `message` | VARCHAR(500) | NOT NULL |
| `created_at` | TIMESTAMP | NOT NULL |
| `sent_at` | TIMESTAMP | - |
