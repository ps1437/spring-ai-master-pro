-- ── Customers Table ───────────────────────────────────
CREATE TABLE IF NOT EXISTS customers (
    id               VARCHAR(36)  PRIMARY KEY,
    name             VARCHAR(100) NOT NULL,
    email            VARCHAR(100) NOT NULL UNIQUE,
    phone            VARCHAR(20),
    tier             VARCHAR(10)  NOT NULL DEFAULT 'BRONZE',
    active_order_id  VARCHAR(36),
    created_at       TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    active           BOOLEAN      NOT NULL DEFAULT TRUE
);

-- ── Orders Table ──────────────────────────────────────
CREATE TABLE IF NOT EXISTS orders (
    order_id           VARCHAR(36)    PRIMARY KEY,
    customer_id        VARCHAR(36)    NOT NULL,
    product_name       VARCHAR(200)   NOT NULL,
    amount             DECIMAL(10, 2) NOT NULL,
    status             VARCHAR(30)    NOT NULL DEFAULT 'PLACED',
    ordered_at         TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    estimated_delivery TIMESTAMP,
    tracking_number    VARCHAR(50),
    shipping_address   VARCHAR(300),

    CONSTRAINT fk_orders_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers (id)
);