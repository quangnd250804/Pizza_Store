-- =========================================================================
-- STEP 1: NẠP DỮ LIỆU DANH MỤC (CATEGORIES)
-- =========================================================================
SET IDENTITY_INSERT categories ON;
INSERT INTO categories (id, name, code, image_url, description, is_active, is_deleted, created_at) VALUES
                                                                                                       (1, N'Pizza Cổ Điển', 'classic', 'https://images.unsplash.com/photo-1513104890138-7c749659a591?q=80&w=600', N'Các dòng bánh truyền thống với lớp phô mai Mozzarella kéo sợi hảo hạng.', 1, 0, GETDATE()),
                                                                                                       (2, N'Pizza Hải Sản', 'seafood', 'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?q=80&w=600', N'Sự kết hợp tươi ngon từ biển cả: tôm, mực, thanh cua cùng sốt Pesto thanh mát.', 1, 0, GETDATE()),
                                                                                                       (3, N'Món Phụ Khai Vị', 'sides', 'https://images.unsplash.com/photo-1573080496219-bb080dd4f877?q=80&w=600', N'Gà giòn bơ tỏi, khoai tây múi cau và bánh mỳ tỏi nướng thơm nức mũi.', 1, 0, GETDATE()),
                                                                                                       (4, N'Thức Uống Giải Nhiệt', 'drinks', 'https://images.unsplash.com/photo-1622483767028-3f66f32aef97?q=80&w=600', N'Đập tan cơn khát với các dòng nước ngọt và trà trái cây mát lạnh.', 1, 0, GETDATE());
SET IDENTITY_INSERT categories OFF;

-- =========================================================================
-- STEP 2: NẠP DỮ LIỆU SẢN PHẨM GỐC (PRODUCTS)
-- =========================================================================
SET IDENTITY_INSERT products ON;
INSERT INTO products (id, category_id, name, description, image_url, is_available, is_deleted, created_at) VALUES
-- Pizza Cổ Điển (ID: 1)
(1, 1, N'Pizza Thập Cẩm Deluxe', N'Xúc xích Pepperoni, thịt giăm bông, hành tây, ớt chuông xanh và ô liu đen phủ phô mai.', 'https://images.unsplash.com/photo-1534308983496-4fabb1a015ee?q=80&w=600', 1, 0, GETDATE()),
(2, 1, N'Pizza Bò Nướng Tiêu Đen', N'Thịt bò băm nướng mềm, sốt tiêu đen đậm đà, hành tây và nấm mỡ nướng thơm ngon.', 'https://images.unsplash.com/photo-1571407970349-bc81e7e96d47?q=80&w=600', 1, 0, GETDATE()),

-- Pizza Hải Sản (ID: 2)
(3, 2, N'Pizza Hải Sản Sốt Pesto', N'Tôm tươi, mực ống, thanh cua, hành tây trên nền sốt lá húng tây Pesto xanh mướt đặc trưng.', 'https://images.unsplash.com/photo-1555072956-7758afb20e8f?q=80&w=600', 1, 0, GETDATE()),

-- Món Phụ (ID: 3)
(4, 3, N'Gà Chiên Giòn Sốt Hàn Quốc', N'4 miếng gà rút xương chiên giòn rụm được phủ lớp sốt cay ngọt kiểu Hàn.', 'https://images.unsplash.com/photo-1569058242253-92a9c755a0ec?q=80&w=600', 1, 0, GETDATE()),
(5, 3, N'Khoai Tây Múi Cau Chiên', N'Khoai tây cắt múi cau nguyên vỏ, tẩm ướp thảo mộc nướng giòn rụm bên ngoài, bùi béo bên trong.', 'https://images.unsplash.com/photo-1573080496219-bb080dd4f877?q=80&w=600', 1, 0, GETDATE()),

-- Đồ Uống (ID: 4)
(6, 4, N'Coca-Cola Original Lon', N'Nước ngọt có ga hương vị truyền thống mát lạnh, lý tưởng khi dùng kèm Pizza.', 'https://images.unsplash.com/photo-1622483767028-3f66f32aef97?q=80&w=600', 1, 0, GETDATE()),
(7, 4, N'Trà Đào Hạt Chia', N'Trà đào thanh ngọt tự nhiên, đi kèm 2 lát đào ngâm giòn ngọt và hạt chia bổ dưỡng.', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?q=80&w=600', 1, 0, GETDATE());
SET IDENTITY_INSERT products OFF;

-- =========================================================================
-- STEP 3: NẠP BIẾN THỂ KÍCH THƯỚC & GIÁ (PRODUCT_VARIANTS)
-- Lưu ý: Đảm bảo bảng `sizes` của bạn đã có 3 dòng ID 1, 2, 3 tương ứng S, M, L
-- =========================================================================
INSERT INTO product_variants (product_id, size_id, price) VALUES
-- Pizza Thập Cẩm Deluxe (ID: 1) -> Đủ 3 size
(1, 1, 129000), -- Size S
(1, 2, 189000), -- Size M
(1, 3, 259000), -- Size L

-- Pizza Bò Nướng Tiêu Đen (ID: 2) -> Đủ 3 size
(2, 1, 139000),
(2, 2, 199000),
(2, 3, 269000),

-- Pizza Hải Sản Sốt Pesto (ID: 3) -> Đủ 3 size
(3, 1, 149000),
(3, 2, 219000),
(3, 3, 289000),

-- Món Phụ và Đồ uống thường chỉ cấu hình 1 size chuẩn mặc định (Ví dụ Size S hoặc Size M)
(4, 1, 69000),   -- Gà chiên
(5, 1, 39000),   -- Khoai tây
(6, 1, 15000),   -- Coca Lon
(7, 2, 29000);   -- Trà Đào (Size M)

-- =========================================================================
-- STEP 4: NẠP DỮ LIỆU COMBO KHUYẾN MÃI (COMBOS & COMBO_DETAILS)
-- =========================================================================
SET IDENTITY_INSERT combos ON;
INSERT INTO combos (id, name, description, image_url, price, is_available, is_deleted, created_at) VALUES
                                                                                                       (1, N'Combo Độc Hành Thịnh Soạn', N'Phù hợp cho 1 người ăn thả ga: Gồm 1 Pizza Thập Cẩm cỡ nhỏ (S), 1 phần khoai tây múi cau và 1 lon Coca mát lạnh.', 'https://images.unsplash.com/photo-1604917621956-10dfa7cce2e7?q=80&w=600', 169000, 1, 0, GETDATE()),
                                                                                                       (2, N'Combo Đôi Bạn Bùng Nổ', N'Bữa tiệc hoàn hảo cho cặp đôi: Gồm 1 Pizza Hải Sản cỡ vừa (M), 1 phần Gà sốt Hàn Quốc và 2 lon Coca-Cola.', 'https://images.unsplash.com/photo-1594212699903-ec8a3eca50f5?q=80&w=600', 289000, 1, 0, GETDATE());
SET IDENTITY_INSERT combos OFF;

-- Thêm chi tiết các món nằm trong Combo (ComboDetails)
INSERT INTO combo_details (combo_id, product_id, quantity) VALUES
-- Combo 1 gồm: 1 Pizza Thập Cẩm (ID 1), 1 Khoai tây (ID 5), 1 Coca (ID 6)
(1, 1, 1),
(1, 5, 1),
(1, 6, 1),

-- Combo 2 gồm: 1 Pizza Hải Sản (ID 3), 1 Gà chiên (ID 4), 2 Coca (ID 6)
(2, 3, 1),
(2, 4, 1),
(2, 6, 2);

select * from categories

SET IDENTITY_INSERT toppings ON;
INSERT INTO toppings (id, name, price, is_available, is_deleted, created_at) VALUES
                                                                                 (1, N'Thêm Phô Mai Mozzarella', 25000, 1, 0, GETDATE()),
                                                                                 (2, N'Thêm Xúc Xích Pepperoni', 20000, 1, 0, GETDATE()),
                                                                                 (3, N'Thêm Thịt Bò Băm', 30000, 1, 0, GETDATE()),
                                                                                 (4, N'Thêm Nấm Mỡ Nướng', 15000, 1, 0, GETDATE());
SET IDENTITY_INSERT toppings OFF;

SET IDENTITY_INSERT products ON;

INSERT INTO products
(id, category_id, name, description, image_url, is_available, is_deleted, created_at)
VALUES

-- ===========================
-- Pizza Cổ Điển (Category 1)
-- ===========================
(8, 1, N'Pizza Pepperoni Phô Mai', N'Pizza truyền thống với xúc xích Pepperoni Mỹ và lớp phô mai Mozzarella phủ kín.', 'https://images.unsplash.com/photo-1513104890138-7c749659a591?q=80&w=600', 1, 0, GETDATE()),

(9, 1, N'Pizza Gà BBQ', N'Thịt gà nướng sốt BBQ đậm vị kết hợp hành tím và phô mai kéo sợi.', 'https://images.unsplash.com/photo-1593560708920-61dd98c46a4e?q=80&w=600', 1, 0, GETDATE()),

(10, 1, N'Pizza Xúc Xích Đức', N'Xúc xích Đức hun khói kết hợp hành tây và sốt cà chua truyền thống.', 'https://images.unsplash.com/photo-1541745537411-b8046dc6d66c?q=80&w=600', 1, 0, GETDATE()),

(11, 1, N'Pizza Nấm Phô Mai', N'Ba loại nấm tươi cùng lớp phô mai Mozzarella béo ngậy.', 'https://images.unsplash.com/photo-1511689660979-10d2b1aada49?q=80&w=600', 1, 0, GETDATE()),

-- ===========================
-- Pizza Hải Sản (Category 2)
-- ===========================
(12, 2, N'Pizza Tôm Phô Mai', N'Tôm tươi kết hợp phô mai Mozzarella và sốt kem hải sản đặc biệt.', 'https://images.unsplash.com/photo-1513104890138-7c749659a591?q=80&w=600', 1, 0, GETDATE()),

(13, 2, N'Pizza Mực Cay', N'Mực tươi cắt khoanh phủ sốt cay Hàn Quốc và phô mai.', 'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?q=80&w=600', 1, 0, GETDATE()),

(14, 2, N'Pizza Hải Sản Nhiệt Đới', N'Tôm, mực cùng thơm tươi mang đến hương vị chua ngọt hấp dẫn.', 'https://images.unsplash.com/photo-1555072956-7758afb20e8f?q=80&w=600', 1, 0, GETDATE()),

-- ===========================
-- Món Phụ (Category 3)
-- ===========================
(15, 3, N'Gà Viên Chiên Giòn', N'Gà viên chiên vàng giòn dùng kèm tương cà và sốt mayonnaise.', 'https://images.unsplash.com/photo-1562967914-608f82629710?q=80&w=600', 1, 0, GETDATE()),

(16, 3, N'Bánh Mì Bơ Tỏi', N'Bánh mì nướng giòn phủ bơ tỏi thơm lừng.', 'https://images.unsplash.com/photo-1509440159596-0249088772ff?q=80&w=600', 1, 0, GETDATE()),

(17, 3, N'Khoai Tây Lắc Phô Mai', N'Khoai tây chiên giòn lắc bột phô mai béo thơm.', 'https://images.unsplash.com/photo-1576107232684-1279f390859f?q=80&w=600', 1, 0, GETDATE()),

-- ===========================
-- Đồ Uống (Category 4)
-- ===========================
(18, 4, N'Sprite Lon', N'Nước ngọt có ga vị chanh tươi mát.', 'https://images.unsplash.com/photo-1624517452488-04869289c4ca?q=80&w=600', 1, 0, GETDATE()),

(19, 4, N'Fanta Cam Lon', N'Nước ngọt vị cam thơm ngon, sảng khoái.', 'https://images.unsplash.com/photo-1581636625402-29b2a704ef13?q=80&w=600', 1, 0, GETDATE()),

(20, 4, N'Trà Chanh Mật Ong', N'Trà chanh thanh mát kết hợp mật ong tự nhiên.', 'https://images.unsplash.com/photo-1499638673689-79a0b5115d87?q=80&w=600', 1, 0, GETDATE());

SET IDENTITY_INSERT products OFF;

INSERT INTO product_variants (product_id, size_id, price)
VALUES

-- Pizza Pepperoni Phô Mai
(8, 1, 139000),
(8, 2, 199000),
(8, 3, 269000),

-- Pizza Gà BBQ
(9, 1, 145000),
(9, 2, 205000),
(9, 3, 275000),

-- Pizza Xúc Xích Đức
(10, 1, 135000),
(10, 2, 195000),
(10, 3, 265000),

-- Pizza Nấm Phô Mai
(11, 1, 125000),
(11, 2, 185000),
(11, 3, 255000),

-- Pizza Tôm Phô Mai
(12, 1, 155000),
(12, 2, 225000),
(12, 3, 295000),

-- Pizza Mực Cay
(13, 1, 149000),
(13, 2, 219000),
(13, 3, 289000),

-- Pizza Hải Sản Nhiệt Đới
(14, 1, 159000),
(14, 2, 229000),
(14, 3, 299000),

-- Món phụ
(15, 1, 59000),
(16, 1, 35000),
(17, 1, 45000),

-- Đồ uống
(18, 1, 15000),
(19, 1, 15000),
(20, 2, 25000);