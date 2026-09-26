-- ============================================================
-- V1__init_schema.sql
-- ระบบจัดการโรงรับจำนำ (Pawnshop Management System)
-- CP353002 Principles of Software Design and Development
-- Target: PostgreSQL 15+
-- ============================================================

-- ---------- ผู้ใช้งานและลูกค้า ----------

CREATE TABLE customer (
    id           BIGSERIAL PRIMARY KEY,
    citizen_id   VARCHAR(13)  NOT NULL,
    first_name   VARCHAR(100) NOT NULL,
    last_name    VARCHAR(100) NOT NULL,
    phone        VARCHAR(20),
    address      VARCHAR(500),
    birth_date   DATE,
    blacklisted  BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP    NOT NULL,
    CONSTRAINT uk_customer_citizen_id UNIQUE (citizen_id)
);
CREATE INDEX idx_customer_phone ON customer (phone);

CREATE TABLE role (
    id   BIGSERIAL PRIMARY KEY,
    code VARCHAR(30)  NOT NULL,
    name VARCHAR(100) NOT NULL,
    CONSTRAINT uk_role_code UNIQUE (code)
);

CREATE TABLE employee (
    id            BIGSERIAL PRIMARY KEY,
    employee_code VARCHAR(20)  NOT NULL,
    first_name    VARCHAR(100) NOT NULL,
    last_name     VARCHAR(100) NOT NULL,
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_employee_code UNIQUE (employee_code)
);

-- Many-to-Many: พนักงาน 1 คนมีได้หลายบทบาท (คะแนนพิเศษตามใบงาน)
CREATE TABLE employee_role (
    employee_id BIGINT NOT NULL,
    role_id     BIGINT NOT NULL,
    PRIMARY KEY (employee_id, role_id),
    CONSTRAINT fk_emprole_employee FOREIGN KEY (employee_id)
        REFERENCES employee (id) ON DELETE CASCADE,
    CONSTRAINT fk_emprole_role FOREIGN KEY (role_id)
        REFERENCES role (id) ON DELETE RESTRICT
);

-- ---------- ทรัพย์และการประเมินราคา ----------

CREATE TABLE pledged_item (
    id                BIGSERIAL PRIMARY KEY,
    item_type         VARCHAR(20)  NOT NULL,
    description       VARCHAR(300) NOT NULL,
    serial_number     VARCHAR(100),
    weight_gram       NUMERIC(10,3),
    purity_percent    NUMERIC(5,2),
    manufacture_year  INTEGER,
    condition_grade   INTEGER,
    storage_slot      VARCHAR(30),
    photo_url         VARCHAR(300)
);
CREATE INDEX idx_item_serial ON pledged_item (serial_number);
CREATE INDEX idx_item_type   ON pledged_item (item_type);

CREATE TABLE gold_price (
    id             BIGSERIAL PRIMARY KEY,
    price_date     DATE          NOT NULL,
    price_per_gram NUMERIC(15,2) NOT NULL,
    recorded_by    BIGINT,
    CONSTRAINT uk_gold_price_date UNIQUE (price_date),
    CONSTRAINT fk_goldprice_employee FOREIGN KEY (recorded_by)
        REFERENCES employee (id) ON DELETE SET NULL
);

-- One-to-One: ทรัพย์ 1 ชิ้นมีผลประเมิน 1 รายการ
CREATE TABLE appraisal (
    id                  BIGSERIAL PRIMARY KEY,
    pledged_item_id     BIGINT        NOT NULL,
    appraiser_id        BIGINT,
    appraised_value     NUMERIC(15,2) NOT NULL,
    max_loan_amount     NUMERIC(15,2) NOT NULL,
    gold_price_snapshot NUMERIC(15,2),
    appraised_at        TIMESTAMP     NOT NULL,
    note                VARCHAR(500),
    CONSTRAINT uk_appraisal_item UNIQUE (pledged_item_id),
    CONSTRAINT fk_appraisal_item FOREIGN KEY (pledged_item_id)
        REFERENCES pledged_item (id) ON DELETE CASCADE,
    CONSTRAINT fk_appraisal_employee FOREIGN KEY (appraiser_id)
        REFERENCES employee (id) ON DELETE SET NULL
);

-- ---------- นโยบายดอกเบี้ย ----------

CREATE TABLE interest_policy (
    id                BIGSERIAL PRIMARY KEY,
    code              VARCHAR(30)  NOT NULL,
    name              VARCHAR(150) NOT NULL,
    effective_from    DATE         NOT NULL,
    effective_to      DATE,
    redemption_months INTEGER      NOT NULL,
    grace_days        INTEGER      NOT NULL,
    CONSTRAINT uk_policy_code UNIQUE (code)
);

-- One-to-Many: นโยบาย 1 ฉบับมีหลายขั้นอัตรา
CREATE TABLE rate_tier (
    id                   BIGSERIAL PRIMARY KEY,
    policy_id            BIGINT        NOT NULL,
    lower_bound          NUMERIC(15,2) NOT NULL,
    upper_bound          NUMERIC(15,2),
    monthly_rate_percent NUMERIC(6,4)  NOT NULL,
    tier_order           INTEGER       NOT NULL,
    CONSTRAINT fk_ratetier_policy FOREIGN KEY (policy_id)
        REFERENCES interest_policy (id) ON DELETE CASCADE
);
CREATE INDEX idx_rate_tier_policy ON rate_tier (policy_id);

-- ---------- ตั๋วจำนำ ----------

-- One-to-One กับ pledged_item, Many-to-One กับ customer
CREATE TABLE pawn_ticket (
    id                  BIGSERIAL PRIMARY KEY,
    ticket_number       VARCHAR(20)   NOT NULL,
    customer_id         BIGINT        NOT NULL,
    pledged_item_id     BIGINT        NOT NULL,
    interest_policy_id  BIGINT        NOT NULL,
    opened_by           BIGINT,
    principal           NUMERIC(15,2) NOT NULL,
    pawn_date           DATE          NOT NULL,
    due_date            DATE          NOT NULL,
    grace_end_date      DATE          NOT NULL,
    interest_paid_until DATE          NOT NULL,
    status              VARCHAR(20)   NOT NULL,
    closed_at           TIMESTAMP,
    seized_reason       VARCHAR(300),
    created_at          TIMESTAMP     NOT NULL,
    updated_at          TIMESTAMP     NOT NULL,
    CONSTRAINT uk_ticket_number UNIQUE (ticket_number),
    CONSTRAINT uk_ticket_item   UNIQUE (pledged_item_id),
    CONSTRAINT fk_ticket_customer FOREIGN KEY (customer_id)
        REFERENCES customer (id) ON DELETE RESTRICT,
    CONSTRAINT fk_ticket_item FOREIGN KEY (pledged_item_id)
        REFERENCES pledged_item (id) ON DELETE RESTRICT,
    CONSTRAINT fk_ticket_policy FOREIGN KEY (interest_policy_id)
        REFERENCES interest_policy (id) ON DELETE RESTRICT,
    CONSTRAINT fk_ticket_employee FOREIGN KEY (opened_by)
        REFERENCES employee (id) ON DELETE SET NULL,
    CONSTRAINT chk_ticket_principal CHECK (principal > 0),
    CONSTRAINT chk_ticket_dates CHECK (due_date >= pawn_date AND grace_end_date >= due_date)
);
CREATE INDEX idx_ticket_status   ON pawn_ticket (status);
CREATE INDEX idx_ticket_customer ON pawn_ticket (customer_id);
CREATE INDEX idx_ticket_due_date ON pawn_ticket (due_date);

-- One-to-Many: ตั๋ว 1 ใบมีหลายรายการธุรกรรม (append-only)
CREATE TABLE ledger_entry (
    id               BIGSERIAL PRIMARY KEY,
    ticket_id        BIGINT        NOT NULL,
    entry_type       VARCHAR(30)   NOT NULL,
    principal_amount NUMERIC(15,2) NOT NULL,
    interest_amount  NUMERIC(15,2) NOT NULL,
    total_amount     NUMERIC(15,2) NOT NULL,
    interest_from    DATE,
    interest_to      DATE,
    entry_date       DATE          NOT NULL,
    handled_by       BIGINT,
    note             VARCHAR(300),
    created_at       TIMESTAMP     NOT NULL,
    CONSTRAINT fk_ledger_ticket FOREIGN KEY (ticket_id)
        REFERENCES pawn_ticket (id) ON DELETE RESTRICT,
    CONSTRAINT fk_ledger_employee FOREIGN KEY (handled_by)
        REFERENCES employee (id) ON DELETE SET NULL
);
CREATE INDEX idx_ledger_ticket ON ledger_entry (ticket_id);
CREATE INDEX idx_ledger_date   ON ledger_entry (entry_date);

-- ---------- ทรัพย์หลุดและการแจ้งเตือน ----------

CREATE TABLE sale_record (
    id              BIGSERIAL PRIMARY KEY,
    pledged_item_id BIGINT        NOT NULL,
    sold_price      NUMERIC(15,2) NOT NULL,
    sold_date       DATE          NOT NULL,
    buyer_name      VARCHAR(200),
    handled_by      BIGINT,
    CONSTRAINT uk_sale_item UNIQUE (pledged_item_id),
    CONSTRAINT fk_sale_item FOREIGN KEY (pledged_item_id)
        REFERENCES pledged_item (id) ON DELETE RESTRICT,
    CONSTRAINT fk_sale_employee FOREIGN KEY (handled_by)
        REFERENCES employee (id) ON DELETE SET NULL
);

CREATE TABLE notification_log (
    id         BIGSERIAL PRIMARY KEY,
    ticket_id  BIGINT,
    channel    VARCHAR(20)  NOT NULL,
    status     VARCHAR(20)  NOT NULL,
    message    VARCHAR(500) NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    sent_at    TIMESTAMP,
    CONSTRAINT fk_notification_ticket FOREIGN KEY (ticket_id)
        REFERENCES pawn_ticket (id) ON DELETE CASCADE
);
CREATE INDEX idx_notification_ticket ON notification_log (ticket_id);
CREATE INDEX idx_notification_status ON notification_log (status);
