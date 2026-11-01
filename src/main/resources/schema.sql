-- ================================================================
-- E-COMMERCE SCHEMA — FULL (v2, đã sửa)
-- Database: PostgreSQL
-- ================================================================
-- Lưu ý:
--   - products không còn price/stock (đã chuyển xuống product_variants)
--   - cart_items, order_items, stock_reservations, stock_movements
--     tham chiếu theo variant_id
--   - TIMESTAMP -> TIMESTAMPTZ toàn bộ
--   - currency FK tới currencies, default 'VND'
--   - sku chỉ unique trong số bản ghi chưa soft-delete

-- 1. CURRENCIES ------------------------------------------------
CREATE TABLE IF NOT EXISTS currencies (
    code          CHAR(3) PRIMARY KEY,
    name          VARCHAR(50) NOT NULL,
    exchange_rate DECIMAL(20, 8) NOT NULL DEFAULT 1,
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. BRANDS ----------------------------------------------------
CREATE TABLE IF NOT EXISTS brands (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    slug        VARCHAR(120) NOT NULL UNIQUE,
    logo_url    VARCHAR(500),
    description VARCHAR(1000),
    country     VARCHAR(100),
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. CATEGORIES ------------------------------------------------
CREATE TABLE IF NOT EXISTS categories (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    slug        VARCHAR(120) NOT NULL UNIQUE,
    description VARCHAR(500),
    parent_id   BIGINT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_category_parent FOREIGN KEY (parent_id)
        REFERENCES categories (id) ON DELETE SET NULL
);

-- 4. SUPPLIERS -------------------------------------------------
CREATE TABLE IF NOT EXISTS suppliers (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name          VARCHAR(200) NOT NULL,
    contact_email VARCHAR(320),
    contact_phone VARCHAR(30),
    address       VARCHAR(500),
    is_active     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 5. PRODUCTS --------------------------------------------------
CREATE TABLE IF NOT EXISTS products (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    category_id BIGINT,
    brand_id    BIGINT,
    name        VARCHAR(200) NOT NULL,
    description VARCHAR(2000),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMPTZ,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id)
        REFERENCES categories (id) ON DELETE SET NULL,
    CONSTRAINT fk_product_brand FOREIGN KEY (brand_id)
        REFERENCES brands (id) ON DELETE SET NULL
);

-- 6. PRODUCT_VARIANTS (giá + tồn kho nằm ở đây) ----------------
CREATE TABLE IF NOT EXISTS product_variants (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_id  BIGINT NOT NULL,
    sku         VARCHAR(100) NOT NULL,
    name        VARCHAR(200),
    attributes  JSONB NOT NULL DEFAULT '{}'::jsonb,
    price       DECIMAL(12, 2) NOT NULL CHECK (price >= 0),
    stock       INTEGER NOT NULL DEFAULT 0 CHECK (stock >= 0),
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMPTZ,
    CONSTRAINT fk_variant_product FOREIGN KEY (product_id)
        REFERENCES products (id) ON DELETE CASCADE
);
-- UNIQUE sku chỉ áp dụng cho bản ghi chưa xóa mềm (xem phần INDEXES)

-- 7. PRODUCT_SUPPLIERS (n-n) -----------------------------------
CREATE TABLE IF NOT EXISTS product_suppliers (
    product_id   BIGINT NOT NULL,
    supplier_id  BIGINT NOT NULL,
    supply_price DECIMAL(12, 2) NOT NULL CHECK (supply_price >= 0),
    PRIMARY KEY (product_id, supplier_id),
    CONSTRAINT fk_ps_product FOREIGN KEY (product_id)
        REFERENCES products (id) ON DELETE CASCADE,
    CONSTRAINT fk_ps_supplier FOREIGN KEY (supplier_id)
        REFERENCES suppliers (id) ON DELETE CASCADE
);

-- 8. USERS -----------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email         VARCHAR(320) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(200) NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 9. ADDRESSES -------------------------------------------------
CREATE TABLE IF NOT EXISTS addresses (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id        BIGINT NOT NULL,
    recipient_name VARCHAR(200) NOT NULL,
    phone          VARCHAR(30) NOT NULL,
    line1          VARCHAR(500) NOT NULL,
    line2          VARCHAR(500),
    city           VARCHAR(100) NOT NULL,
    postal_code    VARCHAR(20),
    country_code   CHAR(2) NOT NULL DEFAULT 'VN',
    is_default     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_address_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE
);

-- 10. ROLES ----------------------------------------------------
CREATE TABLE IF NOT EXISTS roles (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200)
);

-- 11. USER_ROLES (n-n) -----------------------------------------
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id)
        REFERENCES roles (id) ON DELETE CASCADE
);

-- 12. REFRESH_TOKENS -------------------------------------------
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rt_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE
);

-- 13. EMAIL_VERIFICATION_TOKENS --------------------------------
CREATE TABLE IF NOT EXISTS email_verification_tokens (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    used_at    TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_evt_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE
);

-- 14. PASSWORD_RESET_TOKENS ------------------------------------
CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    used_at    TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_prt_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE
);

-- 15. CART_ITEMS (theo variant) ---------------------------------
CREATE TABLE IF NOT EXISTS cart_items (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    variant_id BIGINT NOT NULL,
    quantity   INTEGER NOT NULL CHECK (quantity > 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_cart_user_variant UNIQUE (user_id, variant_id),
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_variant FOREIGN KEY (variant_id)
        REFERENCES product_variants (id) ON DELETE CASCADE
);

-- 16. COUPONS --------------------------------------------------
CREATE TABLE IF NOT EXISTS coupons (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code             VARCHAR(50) NOT NULL UNIQUE,
    discount_type    VARCHAR(20) NOT NULL CHECK (discount_type IN ('percent', 'fixed')),
    discount_value   DECIMAL(12, 2) NOT NULL CHECK (discount_value > 0),
    max_uses         INTEGER,
    used_count       INTEGER NOT NULL DEFAULT 0,
    min_order_amount DECIMAL(12, 2) NOT NULL DEFAULT 0,
    starts_at        TIMESTAMPTZ NOT NULL,
    expires_at       TIMESTAMPTZ,
    is_active        BOOLEAN NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 17. ORDERS ---------------------------------------------------
CREATE TABLE IF NOT EXISTS orders (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    shipping_address_id BIGINT,
    coupon_id           BIGINT,
    subtotal            DECIMAL(12, 2) NOT NULL DEFAULT 0 CHECK (subtotal >= 0),
    discount_amount     DECIMAL(12, 2) NOT NULL DEFAULT 0 CHECK (discount_amount >= 0),
    shipping_fee        DECIMAL(12, 2) NOT NULL DEFAULT 0 CHECK (shipping_fee >= 0),
    total               DECIMAL(12, 2) NOT NULL DEFAULT 0 CHECK (total >= 0),
    currency            CHAR(3) NOT NULL DEFAULT 'VND',
    status              VARCHAR(30) NOT NULL DEFAULT 'pending'
                        CHECK (status IN ('pending', 'paid', 'shipped', 'delivered', 'cancelled')),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_order_address FOREIGN KEY (shipping_address_id)
        REFERENCES addresses (id) ON DELETE SET NULL,
    CONSTRAINT fk_order_coupon FOREIGN KEY (coupon_id)
        REFERENCES coupons (id) ON DELETE SET NULL,
    CONSTRAINT fk_order_currency FOREIGN KEY (currency)
        REFERENCES currencies (code)
);

-- 18. ORDER_ITEMS (snapshot giá theo variant) -------------------
CREATE TABLE IF NOT EXISTS order_items (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id     BIGINT NOT NULL,
    variant_id   BIGINT,
    product_name VARCHAR(200) NOT NULL,
    variant_name VARCHAR(200),
    attributes   JSONB,
    unit_price   DECIMAL(12, 2) NOT NULL CHECK (unit_price >= 0),
    quantity     INTEGER NOT NULL CHECK (quantity > 0),
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id)
        REFERENCES orders (id),
    CONSTRAINT fk_order_item_variant FOREIGN KEY (variant_id)
        REFERENCES product_variants (id) ON DELETE SET NULL
);

-- 19. STOCK_RESERVATIONS (chống oversell, theo variant) --------
CREATE TABLE IF NOT EXISTS stock_reservations (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id    BIGINT NOT NULL,
    variant_id  BIGINT NOT NULL,
    quantity    INTEGER NOT NULL CHECK (quantity > 0),
    expires_at  TIMESTAMPTZ NOT NULL,
    released_at TIMESTAMPTZ,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sr_order FOREIGN KEY (order_id)
        REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT fk_sr_variant FOREIGN KEY (variant_id)
        REFERENCES product_variants (id) ON DELETE CASCADE
);

-- 20. STOCK_MOVEMENTS (log tồn kho, theo variant) --------------
CREATE TABLE IF NOT EXISTS stock_movements (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    variant_id    BIGINT NOT NULL,
    change_amount INTEGER NOT NULL,
    reason        VARCHAR(30) NOT NULL
                  CHECK (reason IN ('purchase', 'sale', 'return', 'adjustment',
                                    'reservation', 'release', 'damage')),
    reference_id  BIGINT,
    note          VARCHAR(500),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sm_variant FOREIGN KEY (variant_id)
        REFERENCES product_variants (id) ON DELETE CASCADE
);

-- 21. PAYMENTS -------------------------------------------------
CREATE TABLE IF NOT EXISTS payments (
    id                     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id               BIGINT NOT NULL,
    amount                 DECIMAL(12, 2) NOT NULL CHECK (amount >= 0),
    currency               CHAR(3) NOT NULL DEFAULT 'VND',
    method                 VARCHAR(30) NOT NULL
                           CHECK (method IN ('cod', 'card', 'bank_transfer',
                                             'momo', 'vnpay', 'paypal', 'stripe')),
    status                 VARCHAR(30) NOT NULL DEFAULT 'pending'
                           CHECK (status IN ('pending', 'succeeded', 'failed', 'refunded')),
    gateway_transaction_id VARCHAR(255),
    paid_at                TIMESTAMPTZ,
    refunded_at            TIMESTAMPTZ,
    created_at             TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT fk_payment_currency FOREIGN KEY (currency)
        REFERENCES currencies (code)
);

-- 22. SHIPMENTS ------------------------------------------------
CREATE TABLE IF NOT EXISTS shipments (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id        BIGINT NOT NULL,
    address_id      BIGINT,
    carrier         VARCHAR(100),
    tracking_number VARCHAR(100),
    status          VARCHAR(30) NOT NULL DEFAULT 'preparing'
                    CHECK (status IN ('preparing', 'shipping', 'delivered',
                                      'failed', 'returned')),
    shipped_at      TIMESTAMPTZ,
    delivered_at    TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_shipment_order FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT fk_shipment_address FOREIGN KEY (address_id)
        REFERENCES addresses (id) ON DELETE SET NULL
);

-- 23. COUPON_REDEMPTIONS ---------------------------------------
CREATE TABLE IF NOT EXISTS coupon_redemptions (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    coupon_id       BIGINT NOT NULL,
    user_id         BIGINT NOT NULL,
    order_id        BIGINT NOT NULL,
    discount_amount DECIMAL(12, 2) NOT NULL CHECK (discount_amount >= 0),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cr_coupon FOREIGN KEY (coupon_id)
        REFERENCES coupons (id) ON DELETE CASCADE,
    CONSTRAINT fk_cr_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_cr_order FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT uq_cr_coupon_user_order UNIQUE (coupon_id, user_id, order_id)
);

-- 24. REVIEWS --------------------------------------------------
CREATE TABLE IF NOT EXISTS reviews (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_id    BIGINT NOT NULL,
    user_id       BIGINT NOT NULL,
    order_item_id BIGINT,
    rating        SMALLINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    title         VARCHAR(200),
    content       VARCHAR(2000),
    is_approved   BOOLEAN NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_product FOREIGN KEY (product_id)
        REFERENCES products (id) ON DELETE CASCADE,
    CONSTRAINT fk_review_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_review_order_item FOREIGN KEY (order_item_id)
        REFERENCES order_items (id) ON DELETE SET NULL,
    CONSTRAINT uq_review_user_product UNIQUE (user_id, product_id)
);

-- 25. WISHLISTS ------------------------------------------------
CREATE TABLE IF NOT EXISTS wishlists (
    user_id    BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, product_id),
    CONSTRAINT fk_wl_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_wl_product FOREIGN KEY (product_id)
        REFERENCES products (id) ON DELETE CASCADE
);

-- 26. NOTIFICATIONS --------------------------------------------
CREATE TABLE IF NOT EXISTS notifications (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    type       VARCHAR(50) NOT NULL,
    title      VARCHAR(200) NOT NULL,
    body       VARCHAR(2000),
    is_read    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notif_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE
);

-- 27. AUDIT_LOGS -----------------------------------------------
CREATE TABLE IF NOT EXISTS audit_logs (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    actor_user_id BIGINT,
    action        VARCHAR(100) NOT NULL,
    entity_type   VARCHAR(50) NOT NULL,
    entity_id     BIGINT NOT NULL,
    old_values    JSONB,
    new_values    JSONB,
    ip_address    INET,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_actor FOREIGN KEY (actor_user_id)
        REFERENCES users (id) ON DELETE SET NULL
);

-- ================================================================
-- DỮ LIỆU GỐC
-- ================================================================
INSERT INTO currencies (code, name, exchange_rate) VALUES
    ('VND', 'Việt Nam Đồng', 1),
    ('USD', 'US Dollar', 25000),
    ('EUR', 'Euro', 27000)
ON CONFLICT (code) DO NOTHING;

INSERT INTO roles (name, description) VALUES
    ('admin',  'Quản trị hệ thống'),
    ('staff',  'Nhân viên vận hành'),
    ('customer', 'Khách hàng')
ON CONFLICT (name) DO NOTHING;

-- ================================================================
-- INDEXES
-- ================================================================
CREATE INDEX IF NOT EXISTS idx_category_parent ON categories(parent_id);
CREATE INDEX IF NOT EXISTS idx_product_brand ON products(brand_id);
CREATE INDEX IF NOT EXISTS idx_product_category ON products(category_id);
CREATE INDEX IF NOT EXISTS idx_variant_product ON product_variants(product_id);
CREATE INDEX IF NOT EXISTS idx_variant_attributes ON product_variants USING GIN (attributes);
CREATE UNIQUE INDEX IF NOT EXISTS uq_variant_sku_active
    ON product_variants (sku) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_ps_supplier ON product_suppliers(supplier_id);
CREATE INDEX IF NOT EXISTS idx_address_user ON addresses(user_id);
CREATE INDEX IF NOT EXISTS idx_ur_role ON user_roles(role_id);
CREATE INDEX IF NOT EXISTS idx_rt_user ON refresh_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_prt_user ON password_reset_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_evt_user ON email_verification_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_cart_user ON cart_items(user_id);
CREATE INDEX IF NOT EXISTS idx_cart_variant ON cart_items(variant_id);
CREATE INDEX IF NOT EXISTS idx_order_user ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_order_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_order_coupon ON orders(coupon_id);
CREATE INDEX IF NOT EXISTS idx_order_item_order ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_item_variant ON order_items(variant_id);
CREATE INDEX IF NOT EXISTS idx_sr_order ON stock_reservations(order_id);
CREATE INDEX IF NOT EXISTS idx_sr_variant ON stock_reservations(variant_id);
CREATE INDEX IF NOT EXISTS idx_sr_expires ON stock_reservations(expires_at) WHERE released_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_sm_variant ON stock_movements(variant_id);
CREATE INDEX IF NOT EXISTS idx_sm_created_at ON stock_movements(created_at);
CREATE INDEX IF NOT EXISTS idx_payment_order ON payments(order_id);
CREATE INDEX IF NOT EXISTS idx_payment_gateway_txn ON payments(gateway_transaction_id);
CREATE INDEX IF NOT EXISTS idx_shipment_order ON shipments(order_id);
CREATE INDEX IF NOT EXISTS idx_shipment_tracking ON shipments(tracking_number);
CREATE INDEX IF NOT EXISTS idx_cr_coupon_user ON coupon_redemptions(coupon_id, user_id);
CREATE INDEX IF NOT EXISTS idx_review_product ON reviews(product_id);
CREATE INDEX IF NOT EXISTS idx_wl_product ON wishlists(product_id);
CREATE INDEX IF NOT EXISTS idx_notif_user_unread ON notifications(user_id) WHERE is_read = FALSE;
CREATE INDEX IF NOT EXISTS idx_audit_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_actor ON audit_logs(actor_user_id);
CREATE INDEX IF NOT EXISTS idx_audit_created_at ON audit_logs(created_at);