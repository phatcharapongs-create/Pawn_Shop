CREATE TABLE user_account (
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(80)  NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    role          VARCHAR(20)  NOT NULL,
    enabled       BOOLEAN      NOT NULL DEFAULT TRUE,
    customer_id   BIGINT UNIQUE,
    employee_id   BIGINT UNIQUE,
    created_at    TIMESTAMP    NOT NULL,
    CONSTRAINT uk_user_account_username UNIQUE (username),
    CONSTRAINT ck_user_account_role CHECK (role IN ('ADMIN', 'CUSTOMER')),
    CONSTRAINT ck_user_account_profile CHECK (customer_id IS NULL OR employee_id IS NULL),
    CONSTRAINT fk_user_account_customer FOREIGN KEY (customer_id)
        REFERENCES customer (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_account_employee FOREIGN KEY (employee_id)
        REFERENCES employee (id) ON DELETE CASCADE
);
