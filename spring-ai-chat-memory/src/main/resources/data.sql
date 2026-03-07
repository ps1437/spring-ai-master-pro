
INSERT INTO customers (id, name, email, phone, tier, active_order_id, active)
VALUES
    ('cust-001', 'John Smith',    'john@example.com',  '+91-9876543210', 'GOLD',   'ORD-001', TRUE),
    ('cust-002', 'Sarah Connor',  'sarah@example.com', '+91-9876543211', 'SILVER', 'ORD-002', TRUE),
    ('cust-003', 'Raj Patel',     'raj@example.com',   '+91-9876543212', 'BRONZE', 'ORD-003', TRUE),
    ('cust-004', 'Emily Watson',  'emily@example.com', '+91-9876543213', 'GOLD',   'ORD-004', TRUE),
    ('cust-005', 'David Lee',     'david@example.com', '+91-9876543214', 'SILVER', 'ORD-005', TRUE);


INSERT INTO orders (order_id, customer_id, product_name, amount, status,
                    ordered_at, estimated_delivery, tracking_number, shipping_address)
VALUES
    -- John (GOLD) — Out for delivery
    ('ORD-001', 'cust-001', 'Dell XPS 15 Laptop',    85000.00, 'OUT_FOR_DELIVERY',
     DATEADD('DAY', -5, CURRENT_TIMESTAMP),
     DATEADD('DAY',  1, CURRENT_TIMESTAMP),
     'TRK-1001', '12 MG Road, Bangalore, Karnataka 560001'),

    -- Sarah (SILVER) — Shipped
    ('ORD-002', 'cust-002', 'Sony WH-1000XM5 Headphones', 28000.00, 'SHIPPED',
     DATEADD('DAY', -3, CURRENT_TIMESTAMP),
     DATEADD('DAY',  2, CURRENT_TIMESTAMP),
     'TRK-1002', '45 Jubilee Hills, Hyderabad, Telangana 500033'),

    -- Raj (BRONZE) — Processing
    ('ORD-003', 'cust-003', 'Samsung Galaxy S24',    45000.00, 'PROCESSING',
     DATEADD('DAY', -1, CURRENT_TIMESTAMP),
     DATEADD('DAY',  4, CURRENT_TIMESTAMP),
     NULL, '78 Anna Nagar, Chennai, Tamil Nadu 600040'),

    -- Emily (GOLD) — Delivered
    ('ORD-004', 'cust-004', 'Apple MacBook Pro M3',  175000.00, 'DELIVERED',
     DATEADD('DAY', -10, CURRENT_TIMESTAMP),
     DATEADD('DAY', -2,  CURRENT_TIMESTAMP),
     'TRK-1004', '23 Bandra West, Mumbai, Maharashtra 400050'),

    -- David (SILVER) — Placed
    ('ORD-005', 'cust-005', 'LG 4K Monitor 27"',     32000.00, 'PLACED',
     CURRENT_TIMESTAMP,
     DATEADD('DAY', 5, CURRENT_TIMESTAMP),
     NULL, '56 Sector 18, Noida, Uttar Pradesh 201301');