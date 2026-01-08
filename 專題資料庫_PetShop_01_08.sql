
use petDB;
-- 資料庫名稱:petDB
-- 帳號:seren
-- 密碼:1234

/* ======================================================================
   PART 1: 建立表格、關聯與觸發器 (DDL)
======================================================================
*/

-- 1. 管理員 (Admin)
CREATE TABLE admin (
    admin_id INT IDENTITY(1,1) PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name NVARCHAR(50) NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL,
    status VARCHAR(10) NOT NULL DEFAULT 'active',
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME NULL DEFAULT GETDATE()
);
GO

CREATE TRIGGER [dbo].[trg_UpdateAdmin] ON [dbo].[admin]
AFTER UPDATE AS
BEGIN
    UPDATE admin SET updated_at = GETDATE()
    WHERE admin_id IN (SELECT admin_id FROM inserted);
END;
GO

-- 2. 會員 (Members)
CREATE TABLE [dbo].[members] (
    [member_id]  INT IDENTITY(1,1) PRIMARY KEY, -- 會員唯一ID
    [email]      VARCHAR(255) UNIQUE NULL,      -- 帳號 
    [password]   VARCHAR(255) NULL,            -- 密碼 (第三方登入時可為空)
    [name]       NVARCHAR(50),                 -- 姓名
    [gender]     CHAR(1),                      -- 性別
    [birthday]   DATE,                         -- 生日
    [phone]      VARCHAR(20) UNIQUE,           -- 手機 (唯一)
    [address]    NVARCHAR(255),                -- 地址
    [picture]    VARCHAR(255),                 -- 大頭照URL
    [status]     VARCHAR(50) DEFAULT 'active', -- 帳號狀態 (active/disabled)
    
    -- 第三方登入 ID (B方案：多重綁定)
    [google_id]  VARCHAR(255) NULL,            -- Google 唯一識別碼
    [line_id]    VARCHAR(255) NULL,            -- Line 唯一識別碼
    
    [points]     INT DEFAULT 0,                -- 會員幣
    [created_at] DATETIME DEFAULT GETDATE(),   -- 建立時間
    [updated_at] DATETIME DEFAULT GETDATE()    -- 最後修改時間
);
GO

CREATE TRIGGER trg_members_update ON [dbo].[members]
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE m SET updated_at = GETDATE()
    FROM [dbo].[members] m INNER JOIN inserted i ON m.member_id = i.member_id;
END;
GO

-- 3. 寵物 (Member Pets)
CREATE TABLE member_pets (
    pet_id INT IDENTITY(1,1) PRIMARY KEY,
    member_id INT NOT NULL,
    pet_name NVARCHAR(50) NULL,
    pet_type NVARCHAR(10) NOT NULL,
    pet_breed NVARCHAR(50) NULL, -- 已整合新增欄位
    pet_age NVARCHAR(10) NOT NULL,
    pet_size NVARCHAR(10) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME NULL DEFAULT GETDATE(),
    CONSTRAINT fk_pet_member FOREIGN KEY ([member_id])
        REFERENCES [dbo].[members] ([member_id])
        ON UPDATE CASCADE ON DELETE CASCADE
);
GO

CREATE TRIGGER trg_member_pets_update ON [dbo].[member_pets]
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE p SET updated_at = GETDATE()
    FROM [dbo].[member_pets] p INNER JOIN inserted i ON p.pet_id = i.pet_id;
END;
GO

-- 4. 優惠券 (Coupons)
CREATE TABLE coupons (
    coupon_id INT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(50) NOT NULL CONSTRAINT UQ_coupons_code UNIQUE,
    discount_type VARCHAR(20) NOT NULL,
    discount_value DECIMAL(10,2) NOT NULL,
    is_limited INT NOT NULL,
    total_amount INT NULL,
    issued_amount INT NULL,
    issue_start_at DATE NOT NULL,
    issue_end_at DATE NULL,
    use_start_at DATE NOT NULL,
    use_end_at DATE NULL,
    min_purchase DECIMAL(10,2) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME NULL DEFAULT GETDATE(),
    status VARCHAR(10) NOT NULL DEFAULT 'active'
);
GO

CREATE TRIGGER trg_coupons_update ON [dbo].[coupons]
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE c SET updated_at = GETDATE()
    FROM [dbo].[coupons] c INNER JOIN inserted i ON c.coupon_id = i.coupon_id;
END;
GO

-- 5. 優惠券領取紀錄 (Coupon Users)
CREATE TABLE coupon_users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    member_id INT NOT NULL,
    coupon_id INT NOT NULL,
    status VARCHAR(10) NOT NULL DEFAULT 'unused',
    assigned_at DATETIME NOT NULL DEFAULT GETDATE(),
    used_at DATETIME NULL,
    CONSTRAINT fk_cu_coupon FOREIGN KEY ([coupon_id])
        REFERENCES [dbo].[coupons] ([coupon_id]) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_cu_member FOREIGN KEY ([member_id])
        REFERENCES [dbo].[members] ([member_id]) ON UPDATE CASCADE ON DELETE CASCADE
);
GO

-- 6. 商品分類 (Categories)
CREATE TABLE categories (
    category_id INT PRIMARY KEY IDENTITY(100,1),
    category_name VARCHAR(80) NOT NULL
);
GO

-- 7. 商品 (Products)
CREATE TABLE products (
    product_id INT IDENTITY(1,1) PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL,
    category_id INT,
    image_url VARCHAR(255),
    expire_date VARCHAR(40),
    is_active BIT DEFAULT 1,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);
GO

-- 商品圖片新增 Trigger (同時寫入 product_images)
CREATE TRIGGER trg_InsertProductImage ON products
AFTER INSERT AS
BEGIN
    INSERT INTO product_images (product_id, image_url, sort_order)
    SELECT product_id, image_url, 1 
    FROM inserted 
    WHERE image_url IS NOT NULL;
END;
GO

-- 8. 商品圖片 (Product Images)
CREATE TABLE product_images (
    image_id INT IDENTITY(1,1) PRIMARY KEY,
    product_id INT,
    image_url VARCHAR(255),
    sort_order INT,
    CONSTRAINT fk_product_image_product FOREIGN KEY (product_id)
        REFERENCES products(product_id) ON DELETE CASCADE
);
GO

-- 9. 收藏清單 (Favorites)
CREATE TABLE favorites (
    favorite_id INT IDENTITY(1,1) PRIMARY KEY,
    member_id INT NOT NULL,
    product_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT fk_fav_member FOREIGN KEY ([member_id])
        REFERENCES [dbo].[members] ([member_id]) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_fav_product FOREIGN KEY (product_id)
        REFERENCES products(product_id) ON DELETE CASCADE,
    CONSTRAINT uq_member_product UNIQUE (member_id, product_id)
);
GO

-- 10. 購物車 (Cart Items)
CREATE TABLE cart_items (
    cart_item_id INT IDENTITY(1,1) PRIMARY KEY,
    member_id INT,
    product_id INT,
    quantity INT NOT NULL,
    price_at_added DECIMAL(10,2),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_member_id_member FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE,
    CONSTRAINT fk_product_id_product FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);
GO

-- 11. 訂單 (Orders)
CREATE TABLE Orders (
    order_id INT IDENTITY(1,1) PRIMARY KEY,
    member_id INT NOT NULL,
    order_date DATETIME2 NOT NULL,
    status NVARCHAR(20) NOT NULL,
    total_amount_undiscount DECIMAL(10,2) NOT NULL,
    coupon_id INT NULL,
    total_amount_discount DECIMAL(10,2) NOT NULL,
    use_points INT DEFAULT 0,
    total_amount_discount_points DECIMAL(10,2) NOT NULL,
    get_points INT DEFAULT 0,
    CONSTRAINT FK_Orders_Members FOREIGN KEY (member_id) REFERENCES members(member_id) ON UPDATE CASCADE ON DELETE NO ACTION,
    CONSTRAINT FK_Orders_Coupons FOREIGN KEY (coupon_id) REFERENCES coupons(coupon_id) ON UPDATE CASCADE ON DELETE SET NULL
);
GO

-- 12. 訂單明細 (OrderItems)
CREATE TABLE Order_Items (
    product_item_id INT IDENTITY(1,1) PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    product_specification NVARCHAR(50) NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    CONSTRAINT FK_OrderItems_Orders FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT FK_OrderItems_Products FOREIGN KEY (product_id) REFERENCES products(product_id) ON UPDATE CASCADE ON DELETE NO ACTION
);
GO

-- 13. 物流 (Shipments)
CREATE TABLE Shipments (
    shipment_id INT IDENTITY(1,1) PRIMARY KEY,
    order_id INT NOT NULL,
    shipping_method NVARCHAR(20) NOT NULL,
    shipping_fee INT NOT NULL,
    tracking_number NVARCHAR(50) NULL,
    shipped_at DATETIME2 NULL,
    delivered_at DATETIME2 NULL,
    status NVARCHAR(20) NULL,
    recipient_name NVARCHAR(50) NOT NULL,
    recipient_phone NVARCHAR(20) NOT NULL,
    shipping_address NVARCHAR(200) NOT NULL,
    CONSTRAINT FK_Shipments_Orders FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON UPDATE CASCADE ON DELETE CASCADE
);
GO

/*========================================================================
   Appointment更新Table
========================================================================*/
-- 14 服務項目表 (service)
CREATE TABLE service (
    service_id INT IDENTITY(1,1) PRIMARY KEY,
    service_name NVARCHAR(100) NOT NULL,
    target_pet_type NVARCHAR(20) NOT NULL,
    target_pet_size NVARCHAR(10),
    description NVARCHAR(255) NULL,
    price DECIMAL(10,2) NOT NULL,
    duration_minutes INT NOT NULL,
    picture NVARCHAR(255),
    is_addon BIT DEFAULT 0 NOT NULL, -- 0:主服務, 1:加購項
    is_active BIT DEFAULT 1 NOT NULL,
    created_at DATETIME DEFAULT GETDATE() NOT NULL,
    updated_at DATETIME DEFAULT GETDATE() NULL
);
GO

-- Trigger: service 自動更新 updated_at
CREATE TRIGGER trg_service_update ON service AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE t SET updated_at = GETDATE() FROM service t INNER JOIN inserted i ON t.service_id = i.service_id;
END;
GO

-- 15 寵物美容師資訊表 (groomer) 
CREATE TABLE groomer (
    groomer_id INT IDENTITY(1,1) PRIMARY KEY,
    --store_id INT NOT NULL,
    groomer_name NVARCHAR(10) NOT NULL,
    phone VARCHAR(30) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    hiredate DATE NOT NULL,
    picture VARCHAR(255) NULL,
    is_active bit DEFAULT 1 NOT NULL, 
    created_at DATETIME DEFAULT GETDATE() NOT NULL,
    updated_at DATETIME DEFAULT GETDATE() NULL,
    
);
GO

-- Trigger: groomer 自動更新 updated_at
CREATE TRIGGER trg_groomer_update ON groomer AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE t SET updated_at = GETDATE() FROM groomer t INNER JOIN inserted i ON t.groomer_id = i.groomer_id;
END;
GO

-- 14 每日排程表 (daily_schedule) -> 依賴 groomer
CREATE TABLE daily_schedule (
    schedule_id INT IDENTITY(1,1) PRIMARY KEY,
    groomer_id INT NOT NULL,
    time_slots VARCHAR(96) DEFAULT REPLICATE('0', 96) NOT NULL, -- 96個字元代表每15分鐘的狀態
    schedule_version INT NOT NULL DEFAULT 1,
    work_date DATE NOT NULL,
    created_at DATETIME DEFAULT GETDATE() NOT NULL,
    updated_at DATETIME DEFAULT GETDATE()  NULL,
    CONSTRAINT FK_Schedule_groomer FOREIGN KEY (groomer_id) REFERENCES groomer(groomer_id),
    CONSTRAINT UQ_groomer_Date UNIQUE (groomer_id, work_date) -- 同一員工同一天只能有一張班表
);
GO

-- Trigger: daily_schedule 自動更新 updated_at
CREATE TRIGGER trg_daily_schedule_update ON daily_schedule AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE t SET updated_at = GETDATE() FROM daily_schedule t INNER JOIN inserted i ON t.schedule_id = i.schedule_id;
END;
GO


-- 15 請假紀錄表 (leave_record) -> 依賴 groomer
CREATE TABLE leave_record (
    leave_id INT IDENTITY(1,1) PRIMARY KEY,
    groomer_id INT NOT NULL,
    leave_date DATE NOT NULL,
    reason NVARCHAR(100) NOT NULL,
    is_active bit DEFAULT 1 NOT NULL,
    created_at DATETIME DEFAULT GETDATE() NOT NULL,
    updated_at DATETIME DEFAULT GETDATE()  NULL,
    CONSTRAINT FK_Leave_groomer FOREIGN KEY (groomer_id) REFERENCES groomer(groomer_id)
    
);
GO

-- Trigger: leave_record 自動更新 updated_at
CREATE TRIGGER trg_leave_record_update ON leave_record AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE t SET updated_at = GETDATE() FROM leave_record t INNER JOIN inserted i ON t.leave_id = i.leave_id;
END;
GO

-- 16 預約訂單表 (appointment) -> 依賴 member_pets, groomer
CREATE TABLE appointment (
    appointment_id INT IDENTITY(1,1) PRIMARY KEY,
    pet_id INT NOT NULL,
    groomer_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    notes NVARCHAR(255) NULL,
    appointment_status NVARCHAR(20) DEFAULT N'預約確認' NOT NULL,
    final_price DECIMAL(10,2) NOT NULL,
    created_at DATETIME DEFAULT GETDATE() NOT NULL,
    updated_at DATETIME DEFAULT GETDATE()  NULL,
    
    -- Foreign Keys
    CONSTRAINT FK_Appt_Pet FOREIGN KEY (pet_id) REFERENCES member_pets(pet_id),
    CONSTRAINT FK_Appt_groomer FOREIGN KEY (groomer_id) REFERENCES groomer(groomer_id),
    
    -- Check Constraints
    CONSTRAINT CHK_Appt_Status CHECK (appointment_status IN (N'預約確認', N'已取消',N'已完成'))
);
GO

-- Trigger: appointment 自動更新 updated_at
CREATE TRIGGER trg_appointment_update ON appointment AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE t SET updated_at = GETDATE() FROM appointment t INNER JOIN inserted i ON t.appointment_id = i.appointment_id;
END;
GO

-- 4.1 預約明細表 (appointment_details) -> 依賴 appointment, service
CREATE TABLE appointment_details (
    detail_id INT IDENTITY(1,1) PRIMARY KEY,
    appointment_id INT NOT NULL,
    service_id INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    duration_minutes INT NOT NULL, -- 記錄當下的服務時間長度
    created_at DATETIME DEFAULT GETDATE() NOT NULL,
    updated_at DATETIME DEFAULT GETDATE()  NULL,
    CONSTRAINT FK_Detail_Appointment FOREIGN KEY (appointment_id) REFERENCES appointment(appointment_id),
    CONSTRAINT FK_Detail_Service FOREIGN KEY (service_id) REFERENCES service(service_id)
);
GO

-- Trigger: appointment_details 自動更新 updated_at
CREATE TRIGGER trg_appointment_details_update ON appointment_details AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE t SET updated_at = GETDATE() FROM appointment_details t INNER JOIN inserted i ON t.detail_id = i.detail_id;
END;
GO




/* ======================================================================
   PART 2: 預存程序與視圖 (Procedures & Views)
======================================================================
*/

-- View: 優惠券與會員使用狀態
CREATE VIEW member_coupon_view AS
SELECT
    cu.id, cu.member_id, cu.coupon_id, cu.status, cu.assigned_at, cu.used_at,
    c.code, c.discount_type, c.discount_value, c.min_purchase, 
    c.issue_start_at, c.issue_end_at, c.use_start_at, c.use_end_at,
    CASE 
        WHEN c.use_end_at < CAST(GETDATE() AS date) THEN 'Y'
        ELSE 'N'
    END AS is_expired
FROM coupon_users cu
JOIN coupons c ON cu.coupon_id = c.coupon_id;
GO

-- View: 收藏清單與商品
CREATE VIEW favorites_products_view AS
SELECT 
    f.favorite_id, f.member_id, f.product_id, f.created_at AS favorite_created_at,
    p.product_name, p.price
FROM favorites f
INNER JOIN products p ON f.product_id = p.product_id;
GO

-- SP: 加入購物車
CREATE PROCEDURE AddToCart
    @member_id INT,
    @product_id INT,
    @quantity INT
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @sum_price DECIMAL(10,2);
    
    SELECT @sum_price = (p.price * @quantity) -- 修正原邏輯：應為商品單價*數量，非購物車數量
    FROM products p 
    WHERE p.product_id = @product_id;

    IF @sum_price IS NULL
    BEGIN
        RAISERROR('Product not found.', 16, 1);
        RETURN;
    END

    INSERT INTO cart_items (member_id, product_id, quantity, price_at_added, created_at, updated_at)
    VALUES (@member_id, @product_id, @quantity, @sum_price, GETDATE(), GETDATE());
END;
GO

-- SP: 新增圖片排序
CREATE PROCEDURE AddProductImage
    @product_id INT,
    @image_url VARCHAR(255)
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @next_order INT;
    SELECT @next_order = ISNULL(MAX(sort_order), 0) + 1
    FROM product_images
    WHERE product_id = @product_id;

    INSERT INTO product_images (product_id, image_url, sort_order)
    VALUES (@product_id, @image_url, @next_order);
END;
GO

/* ======================================================================
   PART 3: 插入測試資料 (DML)
======================================================================
*/

--Admin
INSERT INTO admin
(email, password, name, phone, role, status, created_at, updated_at)
VALUES
('superadmin@gmail.com',
 '$2a$10$rDk5ag/ZFko.eKWVd5NaDuVCpHb0un3NjIE9G3pAhQ9BrraIQVD9a',
 N'超級管理員',
 '0912345678',
 'super_admin',
 'active',
 '2025-12-08 10:16:19.780',
 '2025-12-08 10:16:19.780'),

('admin@gmail.com',
 '$2a$10$M0butpArl868uGBWcB65Z.0CnmMpqq7Pc8wA9JUmgfcIKBLZhrLo.',
 N'一般管理員',
 '0922222222',
 'admin',
 'active',
 '2025-12-08 10:16:19.780',
 '2025-12-08 10:24:20.407');


-- Members
INSERT INTO [dbo].[members] 
    ([email], [password], [name], [gender], [birthday], [phone], [address], [picture], [status], [google_id], [line_id], [points], [created_at], [updated_at])
VALUES
('tom01@gmail.com', '$2b$10$eImiTXuWVxfM37uY4JANjQ==.l1cWQsOgqFm0bYy2hlDq', N'王小明', 'M', '1997-09-10', '0912345001', N'台北市大安區仁愛路1號', 'https://images.pexels.com/photos/3687770/pexels-photo-3687770.jpeg', 'active', NULL, NULL, 120, '2025-11-01', '2025-11-20'),
('amy02@gmail.com', '$2b$10$eImiTXuWVxfM37uY4JANjQ==.l1cWQsOgqFm0bYy2hlD3', N'陳靜怡', 'F', '1998-05-21', '0912345002', N'新北市板橋區文化路20號', 'https://images.pexels.com/photos/2607544/pexels-photo-2607544.jpeg', 'active', 'G123984', NULL, 80, '2025-11-02', '2025-11-22'), -- 歸類到 google_id
('kevin03@gmail.com', '$2b$10$eImiTXuWVxfM37uY4JANjQ==.l1cWQsOgqFm0bYy2hlDq8', N'李承恩', 'M', '1995-12-03', '0912345003', N'桃園市中壢區中央西路35號', 'https://images.pexels.com/photos/2253275/pexels-photo-2253275.jpeg', 'disabled', NULL, NULL, 0, '2025-11-03', '2025-11-05'),
('eva04@gmail.com', '$2b$10$eImiTXuWVxfM37uY4JANjQ==.l1cWQsOgqFm0bYy2hlDq1', N'林玟琪', 'F', '1999-03-17', '0912345004', N'台中市西屯區文心路89號', 'https://images.pexels.com/photos/3687770/pexels-photo-3687770.jpeg', 'active', NULL, NULL, 300, '2025-11-04', '2025-11-23'),
('jason05@gmail.com', '$2b$10$eImiTXuWVxfM37u74JANjQ==.l1cWQsOgqFm0bYy2hlDq', N'周柏翰', 'M', '1996-07-28', '0912345005', N'台南市中西區健康路108號', 'https://images.pexels.com/photos/1458916/pexels-photo-1458916.jpeg', 'active', NULL, 'L998421', 50, '2025-11-05', '2025-11-23'), -- 歸類到 line_id
('lily06@gmail.com', '$2b$10$eImiTXu9VxfM37uY4JANjQ==.l1cWQsOgqFm0bYy2hlDq', N'何庭瑄', 'F', '2000-01-02', '0912345006', N'高雄市苓雅區光華一路12號', 'https://images.pexels.com/photos/485294/pexels-photo-485294.jpeg', 'active', NULL, NULL, 200, '2025-11-06', '2025-11-22'),
('mark07@gmail.com', '$2b$10$eImiTXuWVxfM37uY4JANjQ==.l1cWQsOgqFm0bYy2hlDq', N'簡子翔', 'M', '1994-10-10', '0912345007', N'基隆市仁愛區忠一路30號', 'https://images.pexels.com/photos/800330/pexels-photo-800330.jpeg', 'active', NULL, NULL, 10, '2025-11-07', '2025-11-08'),
('sara08@gmail.com', '$2b$10$eImi6XuWVxfM37uY4JANjQ==.l1cWQsOgqFm0bYy2hlDq', N'戴語晴', 'F', '1997-02-14', '0912345008', N'新竹市東區食品路19號', 'https://images.pexels.com/photos/1629781/pexels-photo-1629781.jpeg', 'active', 'G112233', NULL, 150, '2025-11-08', '2025-11-21'), -- 歸類到 google_id
('leo09@gmail.com', '$2b$10$eImiTXuW5xfM37uY4JANjQ==.l1cWQsOgqFm0bYy2hlDq', N'黃彥博', 'M', '1993-09-30', '0912345009', N'彰化縣員林市三民東路11號', 'https://images.pexels.com/photos/3687770/pexels-photo-3687770.jpeg', 'disabled', NULL, NULL, 0, '2025-11-09', '2025-11-19'),
('ruby10@gmail.com', '$2b$10$e34iTXuWVxfM37uY4JANjQ==.l1cWQsOgqFm0bYy2hlDq', N'張榆涵', 'F', '2001-06-18', '0912345010', N'嘉義市東區民生南路88號', 'https://images.pexels.com/photos/4681107/pexels-photo-4681107.jpeg', 'active', NULL, NULL, 500, '2025-11-10', '2025-11-23');
GO
-- Member Pets (已整合品種資料)
INSERT INTO member_pets (member_id, pet_name, pet_type, pet_breed, pet_age, pet_size, created_at, updated_at)
VALUES
(1, N'花花', N'狗', N'博美', N'幼年', N'小型', '2025-11-01', '2025-11-23'),
(2, N'咪咪', N'貓', N'英短', N'成年', N'小型', '2025-11-02', '2025-11-23'),
(3, N'旺旺', N'狗', N'拉布拉多', N'老年', N'大型', '2025-11-03', '2025-11-23'),
(4, N'球球', N'貓', N'虎斑', N'幼年', N'小型', '2025-11-04', '2025-11-23'),
(5, N'豆豆', N'狗', N'柴犬', N'成年', N'中型', '2025-11-05', '2025-11-23'),
(6, N'雪球', N'貓', N'三花', N'老年', N'小型', '2025-11-06', '2025-11-23'),
(7, N'毛毛', N'狗', N'米克斯', N'幼年', N'中型', '2025-11-07', '2025-11-23'),
(8, N'妮妮', N'貓', N'曼赤肯', N'成年', N'小型', '2025-11-08', '2025-11-23'),
(9, N'黑豆', N'狗', N'黃金獵犬', N'老年', N'大型', '2025-11-09', '2025-11-23'),
(10, N'小白', N'貓', N'波斯貓', N'幼年', N'小型', '2025-11-10', '2025-11-23'),
(3, N'小橘', N'貓', N'橘貓', N'幼年', N'小型', '2025-12-01', '2025-12-06'),
(7, N'黑妞', N'狗', N'拉布拉多', N'成年', N'中型', '2025-12-02', '2025-12-06');

-- Coupons
INSERT INTO coupons (code, discount_type, discount_value, is_limited, total_amount, issued_amount, issue_start_at, issue_end_at, use_start_at, use_end_at, min_purchase)
VALUES
('C00001', 'amount', 100, 0, NULL, NULL, '2025-11-23', '2025-11-28', '2025-11-23', '2025-12-31', 1000),
('C00002', 'percent', 0.90, 1, 300, 120, '2025-10-23', '2025-11-30', '2025-10-23', '2025-12-31', 800),
('C00003', 'amount', 50, 0, NULL, NULL, '2025-11-23', '2025-11-30', '2025-11-23', '2026-01-31', 500);

-- Coupon Users (範例邏輯)
DECLARE @i INT = 1;
WHILE @i <= 30
BEGIN
    DECLARE @rand_member INT = FLOOR(RAND() * 10) + 1;
    DECLARE @rand_coupon INT = FLOOR(RAND() * 3) + 1;
    INSERT INTO coupon_users (member_id, coupon_id, status) VALUES (@rand_member, @rand_coupon, 'unused');
    SET @i = @i + 1;
END;

-- Categories
INSERT INTO categories (category_name) VALUES
('狗狗食品'), ('貓咪食品'), ('貓狗零食'), ('寵物清潔'), ('寵物玩具'), ('保健用品');

-- Products (Trigger 會自動新增 Product Images)
INSERT INTO products 
(product_name, description, price, stock, category_id, image_url, expire_date, is_active, created_at, updated_at)
VALUES
('95%FANTASTIC 犬用鮮肉主食罐 80克【草飼牛肉】(6入)(狗主食罐)', '100%無膠類不含K3及防腐劑符合NRC/AAFCO成幼犬貓營養標準進貨證明、檢驗報告全部公開透明。', 
294, 30, 100, 'https://res.cloudinary.com/dwzbhnqmq/image/upload/v1767621883/pet_shop_products/file_kijf7c.jpg', '2026-05-01', 1, GETDATE(), GETDATE()),
('火雞肉低敏全犬主食罐 374克 (1入)(狗主食罐)', '每日食用量建議依照寵物體型大小、食量情況及所需熱量等，並定時定量作為主食餵食。', 
142, 50, 100, 'https://res.cloudinary.com/dwzbhnqmq/image/upload/v1767622003/pet_shop_products/file_jifofv.jpg', '2026-05-01', 1, GETDATE(), GETDATE()),
('貓寶寶無膠主食罐82克【比目魚】(1入)(貓主食罐)', '專為幼貓生理機能設計', 
48, 40, 101, 'https://res.cloudinary.com/dwzbhnqmq/image/upload/v1767622114/pet_shop_products/file_jjmkp1.webp', '2026-05-01', 1, GETDATE(), GETDATE()),
('無穀成貓主食鮮味杯3.5oz【鮪魚】(6入)(貓主食罐)', '野生鮪魚、蔬果與濃郁湯汁的絕妙組合', 
390, 35, 101,'https://res.cloudinary.com/dwzbhnqmq/image/upload/v1767622174/pet_shop_products/file_c3smqi.jpg', '2026-05-01', 1, GETDATE(), GETDATE()),
('啾嚕迷你捲心塊 雞肉+軟骨 (12克3入)(狗零食)', '一口大小的零食，易於贈送和食用。', 
110, 10, 102, 'https://res.cloudinary.com/dwzbhnqmq/image/upload/v1767622266/pet_shop_products/file_fvxzih.webp', '2026-05-01', 1, GETDATE(), GETDATE()),
('每日貓肉泥 (15克4入)【水曜日-鮪魚+蟹肉】(1包) (貓零食)', '鮪魚製作，口性絕佳。多種口味，讓挑嘴貓也愛不釋手。', 
86, 14, 102,'https://res.cloudinary.com/dwzbhnqmq/image/upload/v1767622301/pet_shop_products/file_i5qndy.jpg', '2026-05-01', 1, GETDATE(), GETDATE()),
('天然驅蟲滴劑-小型犬 (2MLX2)', '不含化學藥性及DEET。有效驅離跳蚤和壁蝨。', 
290, 3, 103, 'https://res.cloudinary.com/dwzbhnqmq/image/upload/v1767622351/pet_shop_products/file_lheuwx.jpg', '2026-05-01', 1, GETDATE(), GETDATE()),
('非藥用除蚤蝨洗毛精 成犬用 250ml', '適合敏感肌膚、洗後可持續一週保護。', 
460, 5, 103, 'https://res.cloudinary.com/dwzbhnqmq/image/upload/v1767622390/pet_shop_products/file_chynl9.jpg', '2026-05-01', 1, GETDATE(), GETDATE()),
('歐寶章魚玩具(30x13.5公分)(狗玩具)', '內含發聲裝置，撕咬玩具時會發聲，吸引狗狗互動。', 
390, 1, 104, 'https://res.cloudinary.com/dwzbhnqmq/image/upload/v1767622714/pet_shop_products/file_k0gh3y.jpg', '2026-05-01', 1, GETDATE(), GETDATE()),
('【顏色隨機出貨不挑款】貓抓板 摩登躺椅(顏色隨機) 672843公分', '摩登時尚躺椅設計,融入家中擺設。', 
460, 5, 104, 'https://res.cloudinary.com/dwzbhnqmq/image/upload/v1767622765/pet_shop_products/file_wslbgm.jpg', '2026-05-01', 1, GETDATE(), GETDATE()),
('腸道機能保養顆粒粉 (30包/盒)(益生菌) (貓狗適用)', '食用腸保益腸道機能保養顆粒粉，有助腸道保養!', 
500, 23, 105, 'https://res.cloudinary.com/dwzbhnqmq/image/upload/v1767622877/pet_shop_products/file_gxpiv4.jpg' ,'2026-05-01', 1, GETDATE(), GETDATE()),
('Fun桑排毛保健粉 50克 (貓保健品)', '溫和排毛四部曲：紓壓、護胃、排毛及良好排便。', 
390, 5, 105, 'https://res.cloudinary.com/dwzbhnqmq/image/upload/v1767622820/pet_shop_products/file_qatqmm.jpg', '2026-05-01', 1, GETDATE(), GETDATE());

-- Favorites
INSERT INTO favorites (member_id, product_id)
VALUES
-- 王小明 (member_id = 1)
(1, 1),
(1, 2),
(1, 4),

-- 陳靜怡 (2)
(2, 3),
(2, 4),

-- 李承恩 (3)
(3, 1),
(3, 2),

-- 林玟琪 (4)
(4, 1),
(4, 3),

-- 周柏翰 (5)
(5, 2),

-- 何庭瑄 (6)
(6, 3),
(6, 4),

-- 簡子翔 (7)
(7, 1),
(7, 4),

-- 戴語晴 (8)
(8, 2),

-- 黃彥博 (9)
(9, 3),

-- 張榆涵 (10)
(10, 1),
(10, 2);



INSERT INTO Orders
(member_id, order_date, status, total_amount_undiscount, coupon_id,
 total_amount_discount, use_points, total_amount_discount_points, get_points)
VALUES
-- 訂單 1（使用優惠券 + 點數）
(1, '2025-12-05 14:30:00', N'已付款',
 736, 1,
 636, 50, 586, 58),

-- 訂單 2（未使用優惠券）
(2, '2025-12-06 16:10:00', N'已付款',
 390, NULL,
 390, 0, 390, 39),

-- 訂單 3（使用折扣百分比券）
(3, '2025-12-07 11:20:00', N'已出貨',
 284, 2,
 256, 0, 256, 25),

-- 訂單 4（取消訂單）
(4, '2025-12-08 18:45:00', N'已取消',
 294, NULL,
 294, 0, 294, 0);
GO
INSERT INTO Order_Items
(order_id, product_id, quantity, unit_price, product_specification, subtotal)
VALUES
-- 訂單 1
(1, 1, 2, 294, N'牛肉口味', 588),
(1, 3, 3, 48,  N'幼貓專用', 144),

-- 訂單 2
(2, 4, 1, 390, N'章魚玩具', 390),

-- 訂單 3
(3, 2, 2, 142, N'低敏配方', 284),

-- 訂單 4
(4, 1, 1, 294, N'牛肉口味', 294);
GO
INSERT INTO Shipments
(order_id, shipping_method, shipping_fee, tracking_number,
 shipped_at, delivered_at, status,
 recipient_name, recipient_phone, shipping_address)
VALUES
-- 訂單 1
(1, N'宅配', 80, 'TCAT20251205001',
 '2025-12-06 10:00:00', '2025-12-07 15:30:00', N'已送達',
 N'王小明', '0912345001', N'台北市大安區仁愛路1號'),

-- 訂單 2
(2, N'超商取貨', 60, 'FAMI20251206001',
 '2025-12-07 09:30:00', '2025-12-08 18:00:00', N'已送達',
 N'陳靜怡', '0912345002', N'新北市板橋區文化路20號'),

-- 訂單 3
(3, N'宅配', 80, 'TCAT20251207001',
 '2025-12-08 11:00:00', NULL, N'配送中',
 N'李承恩', '0912345003', N'桃園市中壢區中央西路35號'),

-- 訂單 4（取消訂單仍保留物流資料）
(4, N'宅配', 80, NULL,
 NULL, NULL, N'已取消',
 N'林玟琪', '0912345004', N'台中市西屯區文心路89號');
GO

CREATE VIEW vw_member_coupons AS
SELECT 
    cu.id AS coupon_user_id,
    cu.member_id,
    cu.coupon_id,
    cu.status AS user_status,
    cu.assigned_at,
    cu.used_at,

    c.code,
    c.discount_type,
    c.discount_value,
    c.is_limited,
    c.total_amount,
    c.issued_amount,
    c.issue_start_at,
    c.issue_end_at,
    c.use_start_at,
    c.use_end_at,
    c.min_purchase,
    c.status AS coupon_status,
    c.created_at,
    c.updated_at
FROM coupon_users cu
JOIN coupons c ON cu.coupon_id = c.coupon_id;



--服務項目
INSERT INTO service (service_name, target_pet_type, target_pet_size, description, price, duration_minutes, is_addon, is_active)
VALUES 
-- =============================================
-- 🐕 狗狗專區 (Dog) - 依體型分類
-- =============================================
('基礎深層洗護(小型犬)', '狗', '小型', '包含雙重洗淨、潤絲、清耳、剪磨指甲、擠肛門腺與腳底毛修剪。', 500, 60,  0, 1),
('基礎深層洗護(中型犬)', '狗', '中型', '針對中型犬的深層清潔，包含基礎美容護理流程。', 800, 90,  0, 1),
('基礎深層洗護(大型犬)', '狗', '大型', '適合黃金獵犬、哈士奇等大型犬，雙人服務確保清潔與安全。', 1200, 120, 0, 1),
('全身精緻造型修剪(小型犬)', '狗', '小型', '包含基礎洗護流程，並由美容師依品種特色進行全身手剪造型。', 1500, 150, 0, 1),
('全身精緻造型修剪(大型犬)', '狗', '大型', '大型犬專屬造型修剪，需提前溝通造型需求。', 2500, 240,  0, 1),

-- 🐕 狗狗加購項 (不分體型)
('狗狗除蚤藥浴', '狗', '不分體型', '使用天然除蚤洗劑，有效驅除體外寄生蟲 (加購項)。', 300, 20, 1, 1),
('足部護理保養', '狗', '不分體型', '針對肉球乾裂進行滋潤保養，修剪腳底雜毛防滑 (加購項)。', 200, 15, 1, 1),
('肛門腺調理', '狗', '不分體型', '專業手法擠壓肛門腺，減少異味與發炎風險 (加購項)。', 150, 10, 1, 1)


--groomer
INSERT INTO groomer (
    groomer_name, 
    phone, 
    email, 
    hiredate 
)
VALUES 
(
    N'陳小美', 
    '0911-001-001', 
    'mei.chen@petdemo.com', 
    '2022-03-15'
);


/* ======================================================================
   Appointment 新增Views&Sp
======================================================================
*/

-- View: 預約清單
CREATE VIEW appointment_list_view AS
SELECT   a.appointment_id, m.name AS member_name, p.pet_name,
                  (SELECT   TOP (1) s.service_name
                 FROM      dbo.appointment_details AS ad INNER JOIN
                               dbo.service AS s ON ad.service_id = s.service_id
                 WHERE    (ad.appointment_id = a.appointment_id) AND (s.is_addon = 0)) AS main_service,
                  (SELECT   STRING_AGG(s.service_name, ', ') AS Expr1
                 FROM      dbo.appointment_details AS ad INNER JOIN
                               dbo.service AS s ON ad.service_id = s.service_id
                 WHERE    (ad.appointment_id = a.appointment_id) AND (s.is_addon = 1)) AS addon_items, a.appointment_date, CONVERT(VARCHAR(5), a.start_time, 108) AS start_time, CONVERT(VARCHAR(5), 
              a.end_time, 108) AS end_time, DATEDIFF(MINUTE, a.start_time, a.end_time) AS duration_minutes, a.notes, a.appointment_status, a.created_at, a.updated_at, e.groomer_name, a.final_price, p.pet_type, 
              p.pet_size, m.phone
FROM     dbo.appointment AS a LEFT OUTER JOIN
              dbo.member_pets AS p ON a.pet_id = p.pet_id LEFT OUTER JOIN
              dbo.members AS m ON p.member_id = m.member_id LEFT OUTER JOIN
              dbo.groomer AS e ON a.groomer_id = e.groomer_id

-- View: 美容師請假
CREATE VIEW leave_record_groomer_view AS
SELECT   l.leave_id, l.groomer_id, g.groomer_name, l.leave_date, l.reason, l.created_at, l.is_active
FROM     dbo.leave_record AS l INNER JOIN
              dbo.groomer AS g ON l.groomer_id = g.groomer_id
GO

-- View: 訂單明細
CREATE VIEW appointment_detail_list_view AS
SELECT   dbo.service.service_name, dbo.appointment_details.detail_id, dbo.appointment_details.appointment_id, dbo.appointment_details.service_id, dbo.appointment_details.price, 
              dbo.appointment_details.duration_minutes, dbo.appointment_details.created_at, dbo.service.is_addon
FROM     dbo.appointment_details INNER JOIN
              dbo.service ON dbo.appointment_details.service_id = dbo.service.service_id
GO

-- SP: 排程生產
CREATE PROCEDURE [dbo].[sp_GenerateGroomerSchedules]
    @GroomerId INT = NULL,       -- 指定美容師 ID 
    @StartDate DATE = NULL,      -- 起始日期 
    @Days INT = 1,               -- 產生天數 (預設為 1 天)
    @OpenTime TIME = '09:00',    -- 預設上班時間
    @CloseTime TIME = '21:00',   -- 預設下班時間
    @WeeklyOffDay INT = -1       -- 每週公休日 
AS
BEGIN
    SET DATEFIRST 7; 
    SET NOCOUNT ON;

    -- 1. 設定起始日
    DECLARE @CurrentDate DATE = ISNULL(@StartDate, CAST(GETDATE() AS DATE));
    
    -- 2. 設定結束日
    DECLARE @EndDate DATE = DATEADD(DAY, @Days - 1, @CurrentDate);

    BEGIN TRY
        BEGIN TRANSACTION;

        -- 使用 WHILE 迴圈產生範圍內的排程
        WHILE @CurrentDate <= @EndDate
        BEGIN
            INSERT INTO daily_schedule (groomer_id, time_slots, work_date, schedule_version, created_at, updated_at)
            SELECT 
                g.groomer_id,
                CASE 
                    -- 判斷每週公休日 (若當天星期幾 - 1 等於 設定的公休日，則全天關閉 '1')
                    WHEN (DATEPART(WEEKDAY, @CurrentDate) - 1) = @WeeklyOffDay THEN REPLICATE('1', 96)
                    -- 正常工作日 (計算開店與閉店的區間)
                    ELSE 
                        REPLICATE('1', Calc.OpenIdx) +                   -- 開店前 (休息)
                        REPLICATE('0', Calc.CloseIdx - Calc.OpenIdx) +   -- 營業中 (可預約)
                        REPLICATE('1', 96 - Calc.CloseIdx)               -- 閉店後 (休息)
                END,
                @CurrentDate,
                1, -- schedule_version
                GETDATE(),
                GETDATE()
            FROM groomer g
            CROSS APPLY (
                SELECT 
                    (DATEPART(HOUR, @OpenTime) * 4) + (DATEPART(MINUTE, @OpenTime) / 15) AS OpenIdx,
                    (DATEPART(HOUR, @CloseTime) * 4) + (DATEPART(MINUTE, @CloseTime) / 15) AS CloseIdx
            ) AS Calc
            WHERE g.is_active = 1 -- 只針對在職美容師
              -- 如果有指定 @GroomerId 就只跑該美容師，否則跑全部
              AND (@GroomerId IS NULL OR g.groomer_id = @GroomerId)
              
              -- 防呆：避免重複插入 (該美容師在該日已有資料則不插)
              AND NOT EXISTS (
                  SELECT 1 FROM daily_schedule ds 
                  WHERE ds.groomer_id = g.groomer_id AND ds.work_date = @CurrentDate
              );

            -- 日期 +1
            SET @CurrentDate = DATEADD(DAY, 1, @CurrentDate);
        END

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END
GO