-- =============================================================
-- SCHEMA
-- =============================================================

CREATE TABLE IF NOT EXISTS customers (
    id          VARCHAR(20) PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(100) NOT NULL,
    phone       VARCHAR(20),
    city        VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS products (
    id          VARCHAR(20) PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    category    VARCHAR(50),
    price       DECIMAL(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS orders (
    id              VARCHAR(20) PRIMARY KEY,
    customer_id     VARCHAR(20) NOT NULL REFERENCES customers(id),
    order_date      DATE NOT NULL,
    status          VARCHAR(30) NOT NULL,
    total_amount    DECIMAL(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS order_items (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id    VARCHAR(20) NOT NULL REFERENCES orders(id),
    product_id  VARCHAR(20) NOT NULL REFERENCES products(id),
    quantity    INT NOT NULL,
    unit_price  DECIMAL(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS cart (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id VARCHAR(20) NOT NULL REFERENCES customers(id),
    product_id  VARCHAR(20) NOT NULL REFERENCES products(id),
    quantity    INT NOT NULL,
    added_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =============================================================
-- CUSTOMERS
-- =============================================================

INSERT INTO customers VALUES
('CUST001', 'Aarav Sharma',    'aarav.sharma@gmail.com',    '+91-98201-11001', 'Mumbai'),
('CUST002', 'Priya Patel',     'priya.patel@gmail.com',     '+91-98202-11002', 'Ahmedabad'),
('CUST003', 'Rohit Verma',     'rohit.verma@gmail.com',     '+91-98203-11003', 'Delhi'),
('CUST004', 'Sneha Iyer',      'sneha.iyer@gmail.com',      '+91-98204-11004', 'Chennai'),
('CUST005', 'Vikram Malhotra', 'vikram.malhotra@gmail.com', '+91-98205-11005', 'Pune');

-- =============================================================
-- PRODUCTS
-- =============================================================

INSERT INTO products VALUES
('PROD001', 'boAt Airdopes 141',        'Electronics',    1299.00),
('PROD002', 'Nike Air Max Running Shoes','Footwear',       7999.00),
('PROD003', 'Prestige Electric Kettle', 'Appliances',     1499.00),
('PROD004', 'Boldfit Yoga Mat',         'Fitness',         799.00),
('PROD005', 'JBL Flip 6 Speaker',       'Electronics',    8999.00),
('PROD006', 'Noise ColorFit Pro 4',     'Electronics',    3499.00),
('PROD007', 'Lakme Face Serum',         'Beauty',          649.00),
('PROD008', 'Wildcraft Backpack 30L',   'Bags',           2199.00),
('PROD009', 'Milton Water Bottle 1L',   'Kitchen',         349.00),
('PROD010', 'Philips Hair Dryer',       'Appliances',     1899.00);

-- =============================================================
-- ORDERS — CUST001 Aarav Sharma
-- =============================================================

INSERT INTO orders VALUES
('ORD001', 'CUST001', '2024-10-05', 'DELIVERED', 9298.00),
('ORD002', 'CUST001', '2024-12-18', 'SHIPPED',   3499.00),
('ORD003', 'CUST001', '2025-01-30', 'PLACED',    1299.00);

INSERT INTO order_items(order_id, product_id, quantity, unit_price) VALUES
('ORD001', 'PROD002', 1, 7999.00),
('ORD001', 'PROD004', 1,  799.00),
('ORD001', 'PROD009', 2,  250.00),
('ORD002', 'PROD006', 1, 3499.00),
('ORD003', 'PROD001', 1, 1299.00);

-- =============================================================
-- ORDERS — CUST002 Priya Patel
-- =============================================================

INSERT INTO orders VALUES
('ORD004', 'CUST002', '2024-09-14', 'DELIVERED', 2148.00),
('ORD005', 'CUST002', '2024-11-11', 'DELIVERED', 8999.00),
('ORD006', 'CUST002', '2025-02-01', 'CANCELLED',  649.00);

INSERT INTO order_items(order_id, product_id, quantity, unit_price) VALUES
('ORD004', 'PROD003', 1, 1499.00),
('ORD004', 'PROD009', 2,  324.50),
('ORD005', 'PROD005', 1, 8999.00),
('ORD006', 'PROD007', 1,  649.00);

-- =============================================================
-- ORDERS — CUST003 Rohit Verma
-- =============================================================

INSERT INTO orders VALUES
('ORD007', 'CUST003', '2024-08-20', 'DELIVERED', 4698.00),
('ORD008', 'CUST003', '2024-12-25', 'DELIVERED', 2199.00),
('ORD009', 'CUST003', '2025-02-10', 'SHIPPED',   1899.00);

INSERT INTO order_items(order_id, product_id, quantity, unit_price) VALUES
('ORD007', 'PROD002', 1, 7999.00),
('ORD007', 'PROD004', 2,  799.00),  -- partial refund scenario
('ORD008', 'PROD008', 1, 2199.00),
('ORD009', 'PROD010', 1, 1899.00);

-- =============================================================
-- ORDERS — CUST004 Sneha Iyer
-- =============================================================

INSERT INTO orders VALUES
('ORD010', 'CUST004', '2024-11-01', 'DELIVERED', 1948.00),
('ORD011', 'CUST004', '2025-01-14', 'PLACED',    8999.00);

INSERT INTO order_items(order_id, product_id, quantity, unit_price) VALUES
('ORD010', 'PROD007', 2,  649.00),
('ORD010', 'PROD009', 2,  324.50),
('ORD011', 'PROD005', 1, 8999.00);

-- =============================================================
-- ORDERS — CUST005 Vikram Malhotra
-- =============================================================

INSERT INTO orders VALUES
('ORD012', 'CUST005', '2024-07-04', 'DELIVERED', 3798.00),
('ORD013', 'CUST005', '2025-01-20', 'SHIPPED',   5698.00);

INSERT INTO order_items(order_id, product_id, quantity, unit_price) VALUES
('ORD012', 'PROD006', 1, 3499.00),
('ORD012', 'PROD009', 1,  299.00),
('ORD013', 'PROD002', 1, 7999.00),  -- partial: 5698 due to coupon
('ORD013', 'PROD004', 1,  799.00);

-- =============================================================
-- CART
-- =============================================================

-- Aarav browsing speakers and beauty
INSERT INTO cart(customer_id, product_id, quantity) VALUES
('CUST001', 'PROD005', 1),
('CUST001', 'PROD007', 2);

-- Priya wants a backpack and yoga mat
INSERT INTO cart(customer_id, product_id, quantity) VALUES
('CUST002', 'PROD008', 1),
('CUST002', 'PROD004', 1);

-- Rohit eyeing smartwatch and kettle
INSERT INTO cart(customer_id, product_id, quantity) VALUES
('CUST003', 'PROD006', 1),
('CUST003', 'PROD003', 1);

-- Sneha looking at hair dryer and earbuds
INSERT INTO cart(customer_id, product_id, quantity) VALUES
('CUST004', 'PROD010', 1),
('CUST004', 'PROD001', 1);

-- Vikram wants a speaker and backpack
INSERT INTO cart(customer_id, product_id, quantity) VALUES
('CUST005', 'PROD005', 1),
('CUST005', 'PROD008', 1);