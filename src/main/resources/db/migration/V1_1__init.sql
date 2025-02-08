CREATE TABLE IF NOT EXISTS ACCOUNT
(

    id             SERIAL PRIMARY KEY,
    user_id        BIGINT      NOT NULL,
    account_number VARCHAR(50) NOT NULL UNIQUE,
    balance        NUMERIC(10, 2) DEFAULT 0.00,
    account_type   VARCHAR(30) NOT NULL,
    status         VARCHAR(30) NOT NULL,
    created_at     TIMESTAMP   NOT NULL DEFAULT now(),
    updated_at     TIMESTAMP   NOT NULL DEFAULT now()

);