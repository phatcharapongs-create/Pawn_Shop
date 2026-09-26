-- ============================================================
-- V2__seed_reference_data.sql
-- ข้อมูลตั้งต้นที่ระบบต้องมีจึงจะทำงานได้
--
-- !!! สำคัญ !!!
-- ตัวเลขอัตราดอกเบี้ย จำนวนเดือน และวันผ่อนผันด้านล่างเป็นค่าตั้งต้นสำหรับ
-- ให้ระบบรันได้เท่านั้น ยังไม่ได้ตรวจสอบกับตัวบทกฎหมาย
-- ก่อนส่งงาน ต้องไปตรวจสอบกับ พ.ร.บ.โรงรับจำนำ ฉบับปัจจุบัน
-- แล้วแก้ตัวเลขให้ตรง พร้อมอ้างอิงมาตราไว้ในรายงานส่วน domain analysis
-- ============================================================

INSERT INTO role (code, name) VALUES
    ('CASHIER',   'พนักงานรับจำนำ'),
    ('APPRAISER', 'ผู้ประเมินราคา'),
    ('MANAGER',   'ผู้จัดการ');

INSERT INTO employee (employee_code, first_name, last_name, active) VALUES
    ('EMP001', 'สมชาย', 'ใจดี',    TRUE),
    ('EMP002', 'สมหญิง', 'รักงาน', TRUE),
    ('EMP003', 'มานะ',  'ตั้งใจ',   TRUE);

-- สมชายเป็นทั้งพนักงานรับจำนำและผู้ประเมิน (แสดงความสัมพันธ์ M:N)
INSERT INTO employee_role (employee_id, role_id) VALUES
    (1, 1), (1, 2),
    (2, 1),
    (3, 3);

-- นโยบายดอกเบี้ยที่บังคับใช้ปัจจุบัน
INSERT INTO interest_policy (code, name, effective_from, effective_to, redemption_months, grace_days)
VALUES ('POLICY-2024', 'อัตราดอกเบี้ยตามกฎหมาย', DATE '2024-01-01', NULL, 4, 30);

-- ขั้นอัตราดอกเบี้ยแบบขั้นบันได คิดแยกชั้นเหมือนภาษีเงินได้
-- ชั้นที่ 1: เงินต้นส่วนแรก        อัตราสูงกว่า
-- ชั้นที่ 2: เงินต้นส่วนที่เกิน      อัตราต่ำกว่า
INSERT INTO rate_tier (policy_id, lower_bound, upper_bound, monthly_rate_percent, tier_order) VALUES
    (1, 0.00,    2000.00, 2.0000,  1),
    (1, 2000.01, NULL,    1.2500,  2);

-- ราคาทองตั้งต้น ผู้จัดการต้องมาอัปเดตรายวันผ่านหน้าจอ
INSERT INTO gold_price (price_date, price_per_gram, recorded_by)
VALUES (CURRENT_DATE, 2500.00, 3);
