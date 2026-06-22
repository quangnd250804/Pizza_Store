CREATE DATABASE pizza_store;
GO

USE pizza_store;
GO

--MODULE 1: AUTHENTICATION AND AUTHORIZATION
CREATE TABLE roles (
	id INT NOT NULL PRIMARY KEY IDENTITY(1,1),
	role_name VARCHAR(50) NOT NULL,
    description NVARCHAR(255)
);

CREATE TABLE users (
    id INT NOT NULL PRIMARY KEY IDENTITY(1,1),
    username NVARCHAR(50) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    full_name NVARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone_number VARCHAR(15) NOT NULL,
    dob DATETIME NOT NULL,
    is_active BIT NOT NULL DEFAULT 1,
    is_deleted BIT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
);

--ALTER TABLE users ALTER COLUMN phone_number VARCHAR(15) NOT NULL;

CREATE TABLE user_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE addresses (
    id INT NOT NULL PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    street NVARCHAR(255) NOT NULL,
    ward NVARCHAR(100) NOT NULL,
    city NVARCHAR(100) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
)

CREATE TABLE refresh_tokens (
    id INT NOT NULL PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
)

-- INSERT 5 ROLES CUSTOMER, ADMIN, KITCHEN, DELIVERY, CASHIER WITH DESCRIPTION
INSERT INTO roles (role_name, description) VALUES ('CUSTOMER', 'Customer role - can order pizza ('),
                                                  ('ADMIN', 'Admin role - can manage users, roles, orders, and addresses'),
                                                  ('KITCHEN', 'Kitchen role - can view and manage orders in the kitchen'),
                                                  ('DELIVERY', 'Delivery role - can view and manage orders in the delivery'),
                                                  ('CASHIER', 'Cashier role - can view and manage orders in the cashier');

--MODULE 2: PRODUCT CATALOG
--Bảng danh mục sản phẩm
CREATE TABLE categories (
	id INT NOT NULL PRIMARY KEY IDENTITY(1,1),
	name NVARCHAR(100) NOT NULL,
    code VARCHAR(10) NOT NULL UNIQUE,
    image_url VARCHAR(500),
	description NVARCHAR(255),
    is_active BIT NOT NULL DEFAULT 1,
    is_deleted BIT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT GETDATE()
);

--Bảng sản phẩm
CREATE TABLE products (
    id INT NOT NULL PRIMARY KEY IDENTITY(1,1),
    category_id INT NOT NULL,
    name NVARCHAR(200) NOT NULL,
    description NVARCHAR(500),
    image_url VARCHAR(500),
    is_available BIT NOT NULL DEFAULT 1,
    is_deleted BIT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    FOREIGN KEY (category_id) REFERENCES categories(id)
)

--Bảng kích thươc sản phẩm
CREATE TABLE sizes (
    id INT NOT NULL PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(50) NOT NULL, -- 'Nhỏ (S)', 'Vừa (M)', 'Lớn (L)'
    code VARCHAR(10) NOT NULL UNIQUE -- 'S', 'M', 'L'
)

INSERT INTO sizes (name, code) VALUES
                                   (N'Nhỏ (S)', 'S'),
                                   (N'Vừa (M)', 'M'),
                                   (N'Lớn (L)', 'L');

--Bang giá sản phẩm theo kích thước
CREATE TABLE product_variants (
    id INT NOT NULL PRIMARY KEY IDENTITY(1,1),
    product_id INT NOT NULL,
    size_id INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (size_id) REFERENCES sizes(id) ON DELETE CASCADE,
    CONSTRAINT UQ_product_size UNIQUE (product_id, size_id) -- Đảm bảo mỗi sản phẩm có một kích thước duy nhất
)

--Bảng topings
CREATE TABLE toppings (
    id INT NOT NULL PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(150) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    is_available BIT NOT NULL DEFAULT 1,
    is_deleted BIT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT GETDATE()
)

--Bảng combo
CREATE TABLE combos (
    id INT NOT NULL PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(200) NOT NULL,
    description NVARCHAR(500),
    image_url VARCHAR(500),
    price DECIMAL(10, 2) NOT NULL,
    is_available BIT NOT NULL DEFAULT 1,
    is_deleted BIT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT GETDATE()
)

--Bảng combo detail liệt kê sản phẩm và số lượng trong combo
CREATE TABLE combo_details
(
    id         INT NOT NULL PRIMARY KEY IDENTITY (1,1),
    combo_id   INT NOT NULL,
    product_id INT NOT NULL,
    quantity   INT NOT NULL DEFAULT 1,
    FOREIGN KEY (combo_id) REFERENCES combos (id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    CONSTRAINT UQ_combo_product UNIQUE (combo_id, product_id) -- Đảm bảo mỗi sản phẩm chỉ xuất hiện một lần trong combo
)

CREATE TABLE orders (
    id INT NOT NULL PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL FOREIGN KEY REFERENCES users(id),
    customer_name NVARCHAR(100) NOT NULL,
    customer_phone VARCHAR(15) NOT NULL,
    shipping_address NVARCHAR(500) NOT NULL,
    note NVARCHAR(500),

    total_price DECIMAL(18, 2) NOT NULL,

    -- Trạng thái đơn hàng: PENDING (Chờ duyệt), CONFIRMED (Đã xác nhận), SHIPPING (Đang giao), DELIVERED (Đã giao), CANCELLED (Đã hủy)
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',

    -- Trạng thái thanh toán: UNPAID (Chưa thanh toán), PAID (Đã thanh toán)
    payment_status VARCHAR(50) NOT NULL DEFAULT 'UNPAID',
    payment_method NVARCHAR(100) NOT NULL, -- Phương thức: COD (Tiền mặt), VNPAY, MOMO...

    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME NULL
)

select * from orders
CREATE TABLE order_details (
    id INT NOT NULL PRIMARY KEY IDENTITY(1,1),
    order_id INT NOT NULL FOREIGN KEY REFERENCES orders(id) ON DELETE CASCADE,
    product_id INT NULL FOREIGN KEY REFERENCES products(id),
    size_id INT NOT NULL FOREIGN KEY REFERENCES sizes(id),

    quantity INT NOT NULL CHECK (quantity > 0),

    -- Giá bán của 1 sản phẩm tại thời điểm mua (Không bao gồm topping)
    -- Tương lai bạn đổi giá sản phẩm ở bảng gốc, giá trong đơn hàng cũ KHÔNG được phép thay đổi theo.
    price DECIMAL(18, 2) NOT NULL
)

-- 1. Thêm cột combo_id để lưu thông tin nếu khách mua combo
ALTER TABLE order_details ADD combo_id INT NULL FOREIGN KEY REFERENCES combos(id);

-- 2. Cho phép size_id được NULL (vì mua combo có thể không cần size)
ALTER TABLE order_details ALTER COLUMN size_id INT NULL;

ALTER TABLE order_details ALTER COLUMN product_id INT NULL;
-- 3. (Tùy chọn) Ràng buộc 1 chi tiết đơn hàng chỉ được phép là Product HOẶC Combo, không được cả 2
ALTER TABLE order_details ADD CONSTRAINT CHK_ProductOrCombo CHECK (
    (product_id IS NOT NULL AND combo_id IS NULL) OR
    (product_id IS NULL AND combo_id IS NOT NULL)
    );

CREATE TABLE order_topping_details (
    id INT NOT NULL PRIMARY KEY IDENTITY(1,1),
    order_detail_id INT NOT NULL FOREIGN KEY REFERENCES order_details(id) ON DELETE CASCADE,
    topping_id INT NOT NULL FOREIGN KEY REFERENCES toppings(id),

    -- Giá topping tại thời điểm mua (Tương tự như giá sản phẩm, để bảo toàn lịch sử hóa đơn)
    price DECIMAL(18,2) NOT NULL
)

CREATE TABLE coupons (
    id              INT            NOT NULL PRIMARY KEY IDENTITY (1,1),
    code            VARCHAR(50)    NOT NULL UNIQUE, -- Mã giảm giá (VD: SALE20)
    description     NVARCHAR(255),
    discount_type   VARCHAR(20)    NOT NULL,        -- 'PERCENT' hoặc 'AMOUNT'
    discount_value  DECIMAL(18, 2) NOT NULL,        -- Giá trị giảm (VD: 20% hoặc 50000đ)
    min_order_value DECIMAL(18, 2),                 -- Đơn hàng tối thiểu để được áp dụng
    valid_from      DATETIME,
    valid_to        DATETIME,
    is_active       BIT            NOT NULL DEFAULT 1,
    created_at      DATETIME       NOT NULL DEFAULT GETDATE()
);

--Insert 3 counpon mẫu
INSERT INTO coupons (code, description, discount_type, discount_value, min_order_value, valid_from, valid_to) VALUES
('SALE20', 'Giảm 20% cho đơn hàng từ 200.000đ', 'PERCENT', 20, 200000, '2024-01-01', '2024-12-31'),
('FLAT50K', 'Giảm 50.000đ cho đơn hàng từ 300.000đ', 'AMOUNT', 50000, 300000, '2024-01-01', '2024-12-31'),
('FREESHIP', 'Miễn phí vận chuyển cho đơn hàng từ 150.000đ', 'AMOUNT', 30000, 150000, '2024-01-01', '2024-12-31');

-- cập nhật lại valid from và valid to của coupon FREESHIP để có thể áp dụng ngay
UPDATE coupons SET valid_from = GETDATE(), valid_to = DATEADD(DAY, 30, GETDATE());

--Insert 3 counpon mẫu nữa
INSERT INTO coupons (code, description, discount_type, discount_value, min_order_value, valid_from, valid_to) VALUES
                                                                                                                  ('SALE40', 'Giảm 20% cho đơn hàng từ 200.000đ', 'PERCENT', 20, 200000, '2024-01-01', '2024-12-31'),
                                                                                                                  ('FLAT80K', 'Giảm 50.000đ cho đơn hàng từ 300.000đ', 'AMOUNT', 50000, 300000, '2024-01-01', '2024-12-31'),
                                                                                                                  ('FREESHIP30', 'Miễn phí vận chuyển cho đơn hàng từ 150.000đ', 'AMOUNT', 30000, 150000, '2024-01-01', '2024-12-31');

select * from coupons

ALTER TABLE orders ADD coupon_id INT NULL FOREIGN KEY REFERENCES coupons(id);
ALTER TABLE orders ADD discount_applied DECIMAL(18, 2) NOT NULL DEFAULT 0; -- Số tiền đã giảm

select * from orders